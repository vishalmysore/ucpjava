ckage io.github.vishalmysore.ucp.server.jsonrpc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * JSON-RPC controller for MCP and A2A transport bindings.
 */
@RestController
@RequestMapping("/ucp/jsonrpc")
public class JsonRpcController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle JSON-RPC requests.
     */
    @PostMapping
    public ResponseEntity<JsonNode> handleJsonRpc(@RequestBody JsonNode request) {
        // TODO: Implement JSON-RPC method dispatching
        // Parse method, params, id
        // Route to appropriate handler (checkout, order, etc.)

        // For now, return a basic response
        try {
            JsonNode response = objectMapper.createObjectNode()
                .put("jsonrpc", "2.0")
                .put("id", request.get("id").asInt())
                .set("result", objectMapper.createObjectNode().put("version", "2026-01-11"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}


