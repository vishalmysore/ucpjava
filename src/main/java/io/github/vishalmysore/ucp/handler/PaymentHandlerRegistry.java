ckage io.github.vishalmysore.ucp.handler;

import io.github.vishalmysore.ucp.annotation.UCPHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for payment handlers.
 * Auto-discovers @UCPHandler annotated classes.
 */
@Component
public class PaymentHandlerRegistry {

    private final Map<String, PaymentHandler> handlers = new HashMap<>();

    public PaymentHandlerRegistry(ApplicationContext context) {
        // Auto-discover handlers
        Map<String, Object> handlerBeans = context.getBeansWithAnnotation(UCPHandler.class);
        for (Object bean : handlerBeans.values()) {
            if (bean instanceof PaymentHandler) {
                UCPHandler annotation = bean.getClass().getAnnotation(UCPHandler.class);
                handlers.put(annotation.name(), (PaymentHandler) bean);
            }
        }
    }

    /**
     * Get a payment handler by name.
     */
    public PaymentHandler getHandler(String name) {
        return handlers.get(name);
    }

    /**
     * Get all registered handlers.
     */
    public Map<String, PaymentHandler> getAllHandlers() {
        return new HashMap<>(handlers);
    }
}


