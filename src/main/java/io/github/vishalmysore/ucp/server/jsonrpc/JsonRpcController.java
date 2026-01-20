package io.github.vishalmysore.ucp.server.jsonrpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t4a.api.AIAction;
import com.t4a.detect.ActionCallback;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.*;

import io.github.vishalmysore.a2a.domain.JsonRpcRequest;
import io.github.vishalmysore.common.server.SpringAwareJSONRpcController;
import io.github.vishalmysore.mcp.domain.CallToolResult;
import io.github.vishalmysore.mcp.domain.Content;
import io.github.vishalmysore.mcp.domain.TextContent;
import io.github.vishalmysore.mcp.domain.ToolCallRequest;
import io.github.vishalmysore.ucp.domain.SimpleUCPResult;
import io.github.vishalmysore.ucp.domain.UCPCallback;
import io.github.vishalmysore.ucp.domain.UCPResult;
import jakarta.servlet.http.HttpServletRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * JSON-RPC controller for MCP and A2A transport bindings.
 */
@RestController
@Log
@RequestMapping("/ucp/")
public class JsonRpcController extends SpringAwareJSONRpcController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AIProcessor baseProcessor = new GeminiV2ActionProcessor();

    @Autowired
    public JsonRpcController(ApplicationContext context) {
        super(context);
        baseProcessor = PredictionLoader.getInstance().createOrGetAIProcessor();
    }

    @PostMapping("mcp")
    @Override
    public Object handleRpc(@RequestBody JsonRpcRequest request, HttpServletRequest httpRequest) {
        // Create ToolCallRequest from UCP Native JSON-RPC request
        ToolCallRequest toolCallRequest = new ToolCallRequest();

        // Map JSON-RPC method (e.g., "io.github.vishalmysore.car_comparison") to tool
        // name
        toolCallRequest.setName(request.getMethod());

        // Map JSON-RPC params to tool arguments
        if (request.getParams() != null && request.getParams() instanceof Map) {
            toolCallRequest.setArguments((Map<String, Object>) request.getParams());
        } else {
            toolCallRequest.setArguments(new HashMap<>());
        }

        // Forward to MCP Tools Controller and get raw response
        Object mcpResponse = callToolWithCallback(toolCallRequest, new UCPCallback());

        // Extract actual data from ResponseEntity if needed
        Object actualResponse = mcpResponse;
        if (mcpResponse instanceof ResponseEntity) {
            actualResponse = ((ResponseEntity<?>) mcpResponse).getBody();
        }

        // If it's a JSONRPCResponse, extract the result
        if (actualResponse != null && actualResponse.getClass().getSimpleName().equals("JSONRPCResponse")) {
            try {
                java.lang.reflect.Method getResultMethod = actualResponse.getClass().getMethod("getResult");
                actualResponse = getResultMethod.invoke(actualResponse);
            } catch (Exception e) {
                // Fallback: use as is
            }
        }

        // Wrap in UCP-compliant format
        Map<String, Object> ucpResult = wrapInUcpFormat(request.getMethod(), actualResponse);

        // Build complete JSON-RPC 2.0 response
        Map<String, Object> jsonRpcResponse = new HashMap<>();
        jsonRpcResponse.put("jsonrpc", "2.0");
        jsonRpcResponse.put("id", request.getId());
        jsonRpcResponse.put("result", ucpResult);

        return jsonRpcResponse;
    }

    protected Object processAction(ToolCallRequest request, ActionCallback callback, AIProcessor processor,
            AIAction action) throws AIProcessingException {
        Object result = processor.processSingleAction(request.toString(), action, new LoggingHumanDecision(),
                new LogginggExplainDecision(), callback);
        return result;
    }

    public AIProcessor getBaseProcessor() {
        return baseProcessor;
    }

    public UCPResult callToolWithCallback(@RequestBody ToolCallRequest request, ActionCallback callback) {
        Map<String, AIAction> predictions = PredictionLoader.getInstance().getPredictions();
        AIAction action = predictions.get(request.getName());
        AIProcessor processor = getBaseProcessor();
        CallToolResult callToolResult = new CallToolResult();
        List<Content> content = new ArrayList<>();

        try {
            callback.setContext(callToolResult);
            Object result = processAction(request, callback, processor, action);

            if (result != null) {
                if (result instanceof UCPResult) {
                    // Return UCPResult directly
                    return (UCPResult) result;
                } else if (result instanceof Content) {
                    content.add((Content) result);
                } else {
                    String resultStr = result.toString();
                    TextContent textContent = new TextContent();
                    textContent.setType("text");
                    textContent.setText(resultStr);
                    content.add(textContent);
                }
            } else {
                TextContent textContent = new TextContent();
                textContent.setType("text");
                textContent.setText("No result available");
                content.add(textContent);
            }
        } catch (AIProcessingException e) {
            // Wrap error in UCPResult
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String errorMessage = "Technical issue occurred while processing the request: " + sw.toString();
            log.severe(sw.toString());
            return new SimpleUCPResult("error", errorMessage);
        }

        // Wrap CallToolResult content in UCPResult
        callToolResult.setContent(content);
        // Extract text from content and return as UCPResult
        if (!content.isEmpty() && content.get(0) instanceof TextContent) {
            TextContent textContent = (TextContent) content.get(0);
            return new SimpleUCPResult("message", textContent.getText());
        }
        return new SimpleUCPResult("result", "No content available");
    }

    /**
     * Wraps MCP tool response in UCP-compliant format with metadata block.
     */
    private Map<String, Object> wrapInUcpFormat(String capabilityName, Object mcpResponse) {
        Map<String, Object> ucpResult = new HashMap<>();

        // Add UCP metadata block
        Map<String, Object> ucpMeta = new HashMap<>();
        ucpMeta.put("version", "2026-01-11");

        // Add capability that was invoked
        Map<String, Object> capability = new HashMap<>();
        capability.put("name", capabilityName);
        capability.put("version", "2026-01-19"); // TODO: Get from registry
        ucpMeta.put("capabilities", new Object[] { capability });

        ucpResult.put("ucp", ucpMeta);

        // Extract business data from MCP response

        // Handle UCPResult directly
        if (mcpResponse instanceof UCPResult) {
            UCPResult ucpResultObject = (UCPResult) mcpResponse;
            Map<String, String> resultData = ucpResultObject.getResult();
            // Merge all fields from UCPResult into the response
            if (resultData != null) {
                resultData.forEach((key, value) -> ucpResult.put(key, value));
            }
            return ucpResult;
        }

        // Handle CallToolResult type
        if (mcpResponse != null && mcpResponse.getClass().getSimpleName().equals("CallToolResult")) {
            try {
                // Get the content list from CallToolResult
                java.lang.reflect.Method getContentMethod = mcpResponse.getClass().getMethod("getContent");
                Object contentObj = getContentMethod.invoke(mcpResponse);

                if (contentObj instanceof java.util.List) {
                    java.util.List<?> contentList = (java.util.List<?>) contentObj;
                    if (!contentList.isEmpty()) {
                        Object firstContent = contentList.get(0);
                        // Extract text from TextContent
                        if (firstContent != null && firstContent.getClass().getSimpleName().equals("TextContent")) {
                            java.lang.reflect.Method getTextMethod = firstContent.getClass().getMethod("getText");
                            Object textObj = getTextMethod.invoke(firstContent);
                            if (textObj != null) {
                                String text = textObj.toString();
                                // Check if this is UCP structured data
                                if (text.startsWith("__UCP_STRUCTURED_DATA__:")) {
                                    try {
                                        String json = text.substring("__UCP_STRUCTURED_DATA__:".length());
                                        Map<String, Object> structuredData = objectMapper.readValue(json, Map.class);
                                        // Merge the structured data into ucpResult
                                        ucpResult.putAll(structuredData);
                                    } catch (Exception e) {
                                        // Fallback to regular message
                                        ucpResult.put("message", text);
                                    }
                                } else {
                                    ucpResult.put("message", text);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Fallback to toString
                ucpResult.put("result", mcpResponse.toString());
            }
        } else if (mcpResponse instanceof Map) {
            Map<String, Object> mcpMap = (Map<String, Object>) mcpResponse;

            // If it's an MCP tool response with "content" array, extract the text
            if (mcpMap.containsKey("content") && mcpMap.get("content") instanceof java.util.List) {
                java.util.List<?> content = (java.util.List<?>) mcpMap.get("content");
                if (!content.isEmpty() && content.get(0) instanceof Map) {
                    Map<String, Object> firstContent = (Map<String, Object>) content.get(0);
                    if (firstContent.containsKey("text")) {
                        // For now, just return the text as a simple result
                        // TODO: Parse structured data from the business logic
                        ucpResult.put("result", firstContent.get("text"));
                    }
                }
            } else {
                // If it's already structured data, merge it in
                for (Map.Entry<String, Object> entry : mcpMap.entrySet()) {
                    if (!"type".equals(entry.getKey()) && !"textResult".equals(entry.getKey())) {
                        ucpResult.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } else if (mcpResponse != null) {
            // Fallback: just return as string
            ucpResult.put("result", mcpResponse.toString());
        }

        return ucpResult;
    }
}
