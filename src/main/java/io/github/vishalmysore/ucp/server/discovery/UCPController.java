package io.github.vishalmysore.ucp.server.discovery;

import com.t4a.api.AIAction;
import com.t4a.api.GenericJavaMethodAction;
import com.t4a.predict.PredictionLoader;
import com.t4a.processor.AIProcessingException;
import com.t4a.transform.PromptTransformer;
import io.github.vishalmysore.a2a.domain.AgentCard;
import io.github.vishalmysore.a2a.domain.JsonRpcRequest;
import io.github.vishalmysore.a2a.server.RealTimeAgentCardController;
import io.github.vishalmysore.a2a.server.SpringAwareAgentCardController;
import io.github.vishalmysore.common.server.SpringAwareJSONRpcController;
import io.github.vishalmysore.mcp.domain.ToolCallRequest;
import io.github.vishalmysore.ucp.annotation.UCPBusiness;
import io.github.vishalmysore.ucp.annotation.UCPCapability;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.*;

@Log
@RestController
@RequestMapping(RealTimeAgentCardController.WELL_KNOWN_PATH)
public class UCPController {

    private PromptTransformer promptTransformer;
    private UCPBusiness detectedBusiness;
    // Map to store discovered capabilities: Key = Capability Name, Value = Metadata
    // Map
    private final Map<String, Map<String, Object>> capabilityRegistry = new HashMap<>();

    @Autowired
    public UCPController(ApplicationContext context) {

    }

    /**
     * The Standard UCP 2026 Manifest Endpoint.
     * Accessible at: {base_url}/.well-known/ucp
     */
    @GetMapping(value = "ucp", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUCPManifest(HttpServletRequest request) {
        if (detectedBusiness == null) {
            log.warning("Manifest requested but no @UCPBusiness was detected in the classpath.");
            return ResponseEntity.notFound().build();
        }

        String baseUrl = ServletUriComponentsBuilder.fromContextPath(request)
                .replacePath(null)
                .build()
                .toUriString();

        Map<String, Object> manifest = new HashMap<>();
        Map<String, Object> ucpData = new HashMap<>();

        ucpData.put("version", "2026-01-19"); // Current Protocol Version
        // Remove merchant_name - not part of UCP spec

        // Define the main Shopping service
        Map<String, Object> serviceDetails = new HashMap<>();
        serviceDetails.put("version", "2026-01-11");
        serviceDetails.put("spec", "https://ucp.dev/specification/overview"); // Correct URL

        Map<String, Object> restDetails = new HashMap<>();
        restDetails.put("endpoint", baseUrl + "/ucp/v1");
        restDetails.put("schema", "https://ucp.dev/services/shopping/openapi.json");

        serviceDetails.put("rest", restDetails);

        Map<String, Object> mcpDetails = new HashMap<>();
        mcpDetails.put("schema", "https://ucp.dev/services/shopping/mcp.openrpc.json");
        mcpDetails.put("endpoint", baseUrl + "/ucp/mcp");
        serviceDetails.put("mcp", mcpDetails);

        Map<String, Object> services = new HashMap<>();
        services.put("dev.ucp.shopping", serviceDetails);

        ucpData.put("services", services);

        // Populate Capabilities from the Registry
        List<Map<String, Object>> capabilityList = new ArrayList<>(capabilityRegistry.values());
        ucpData.put("capabilities", capabilityList);

        manifest.put("ucp", ucpData);
        return ResponseEntity.ok(manifest);
    }

    @PostConstruct
    public void init() {
        this.promptTransformer = PredictionLoader.getInstance().createOrGetPromptTransformer();
        Map<String, AIAction> predictions = PredictionLoader.getInstance().getPredictions();

        Set<Class<?>> distinctBusinesses = new HashSet<>();
        Set<Class<?>> ucpAwareImplementations = new HashSet<>();
        List<String> capabilityLogEntries = new ArrayList<>();
        boolean ucpStandardRegistered = false;

        for (AIAction action : predictions.values()) {
            if (action instanceof GenericJavaMethodAction) {
                Method method = ((GenericJavaMethodAction) action).getActionMethod();
                Class<?> clazz = method.getDeclaringClass();
                String jsonStrForParameters = action.getActionParameters();
                log.info(jsonStrForParameters)
                // 1. Detect the Business (Header Identity)
                if (clazz.isAnnotationPresent(UCPBusiness.class)) {
                    UCPBusiness business = clazz.getAnnotation(UCPBusiness.class);
                    distinctBusinesses.add(clazz);
                    this.detectedBusiness = business;
                }

                // 2. Check for UCPAware implementation
                if ((!ucpStandardRegistered) && (io.github.vishalmysore.ucp.domain.discovery.UCPAware.class.isAssignableFrom(clazz))) {
                    ucpAwareImplementations.add(clazz);
                    ucpStandardRegistered= true;
                }

                // 3. Scan Methods for UCP Capabilities
                if (method.isAnnotationPresent(UCPCapability.class)) {
                    UCPCapability cap = method.getAnnotation(UCPCapability.class);

                    // Validate version format
                    if (!cap.version().matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                        throw new IllegalStateException(
                                "Invalid capability version format: " + cap.version() +
                                        ". Must be YYYY-MM-DD");
                    }

                    Map<String, Object> capDetails = new HashMap<>();
                    capDetails.put("name", cap.name());
                    capDetails.put("version", cap.version());
                    if (!cap.spec().isEmpty())
                        capDetails.put("spec", cap.spec());
                    if (!cap.schema().isEmpty())
                        capDetails.put("schema", cap.schema());
                    // Enforcement: Agents must be RestControllers to support mandatory REST
                    // transport
                    if (!clazz.isAnnotationPresent(RestController.class)) {
                        throw new IllegalStateException("UCP Violation: Agent " + clazz.getSimpleName()
                                + " must be annotated with @RestController to support REST transport.");
                    }

                    capabilityRegistry.put(cap.name(), capDetails);
                    capabilityLogEntries.add(cap.name() + " -> " + method.getName());
                }
            }
        }

        // Enforcement: UCP 2026 requires a single Merchant of Record per host
        if (distinctBusinesses.size() > 1) {
            throw new IllegalStateException("UCP Violation: Multiple businesses found: " + distinctBusinesses
                    + ". Each host must represent exactly one merchant.");
        }

        // Enforcement: Only one UCPAware implementation per host
        if (ucpAwareImplementations.size() > 1) {
            throw new IllegalStateException(
                    "UCP Violation: Multiple UCPAware implementations found: " + ucpAwareImplementations
                            + ". Each host must implement exactly one UCPAware.");
        }

        if (detectedBusiness != null) {
            log.info("UCP Identity Initialized: " + detectedBusiness.name());
            if (!ucpAwareImplementations.isEmpty()) {
                log.info("UCPAware Implementation: " + ucpAwareImplementations.iterator().next().getSimpleName());
                registerStandardCapabilities();
            }
            log.info("Registered Capabilities: " + capabilityLogEntries);

        }
    }

    private void registerStandardCapabilities() {
        // Checkout
        Map<String, Object> checkout = new HashMap<>();
        checkout.put("name", "dev.ucp.shopping.checkout");
        checkout.put("version", "2026-01-11");
        checkout.put("spec", "https://ucp.dev/specification/checkout");
        checkout.put("schema", "https://ucp.dev/schemas/shopping/checkout.json");
        capabilityRegistry.put("dev.ucp.shopping.checkout", checkout);

        // Order
        Map<String, Object> order = new HashMap<>();
        order.put("name", "dev.ucp.shopping.order");
        order.put("version", "2026-01-11");
        order.put("spec", "https://ucp.dev/specification/order");
        order.put("schema", "https://ucp.dev/schemas/shopping/order.json");
        capabilityRegistry.put("dev.ucp.shopping.order", order);

        // Identity Linking
        Map<String, Object> identity = new HashMap<>();
        identity.put("name", "dev.ucp.common.identity_linking");
        identity.put("version", "2026-01-11");
        identity.put("spec", "https://ucp.dev/specification/identity-linking");
        identity.put("schema", "https://ucp.dev/schemas/common/identity_linking.json");
        capabilityRegistry.put("dev.ucp.common.identity_linking", identity);
    }

}