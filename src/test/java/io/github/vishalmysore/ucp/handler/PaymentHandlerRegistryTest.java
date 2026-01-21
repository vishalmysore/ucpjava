package io.github.vishalmysore.ucp.handler;

import io.github.vishalmysore.ucp.annotation.UCPHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentHandlerRegistryTest {

    @Mock
    private ApplicationContext context;

    @Mock
    private PaymentHandler mockHandler;

    @Test
    void testRegistryInitialization() {
        TestPaymentHandler handler = new TestPaymentHandler();
        when(context.getBeansWithAnnotation(UCPHandler.class))
            .thenReturn(Map.of("testHandlerBean", handler));

        PaymentHandlerRegistry registry = new PaymentHandlerRegistry(context);

        assertNotNull(registry.getHandler("testHandler"));
        assertEquals(handler, registry.getHandler("testHandler"));
    }

    @Test
    void testGetAllHandlers() {
        when(context.getBeansWithAnnotation(UCPHandler.class)).thenReturn(Map.of());

        PaymentHandlerRegistry registry = new PaymentHandlerRegistry(context);

        Map<String, PaymentHandler> handlers = registry.getAllHandlers();
        assertNotNull(handlers);
        assertTrue(handlers.isEmpty());
    }

    @Test
    void testGetHandler_NotFound() {
        when(context.getBeansWithAnnotation(UCPHandler.class)).thenReturn(Map.of());

        PaymentHandlerRegistry registry = new PaymentHandlerRegistry(context);

        assertNull(registry.getHandler("nonexistent"));
    }
}