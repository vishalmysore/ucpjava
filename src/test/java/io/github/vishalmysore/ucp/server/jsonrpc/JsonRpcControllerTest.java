package io.github.vishalmysore.ucp.server.jsonrpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vishalmysore.ucp.domain.SimpleUCPResult;
import io.github.vishalmysore.ucp.domain.UCPResult;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonRpcControllerTest {

    @Test
    void testWrapInUcpFormat_SimpleUCPResult() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        Environment mockEnv = mock(Environment.class);
        when(mockContext.getEnvironment()).thenReturn(mockEnv);

        JsonRpcController controller = new JsonRpcController(mockContext);
        SimpleUCPResult result = new SimpleUCPResult("testKey", "testValue");

        Map<String, Object> wrapped = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
                controller, "wrapInUcpFormat", "test.capability", result);

        assertNotNull(wrapped);
        assertTrue(wrapped.containsKey("ucp"));
        assertEquals("testValue", wrapped.get("testKey"));

        Map<String, Object> ucpMeta = (Map<String, Object>) wrapped.get("ucp");
        assertEquals("2026-01-11", ucpMeta.get("version"));
    }

    @Test
    void testWrapInUcpFormat_Map() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        Environment mockEnv = mock(Environment.class);
        when(mockContext.getEnvironment()).thenReturn(mockEnv);

        JsonRpcController controller = new JsonRpcController(mockContext);
        Map<String, Object> result = Map.of("key1", "value1");

        Map<String, Object> wrapped = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
                controller, "wrapInUcpFormat", "test.capability", result);

        assertNotNull(wrapped);
        assertEquals("value1", wrapped.get("key1"));
    }

    @Test
    void testWrapInUcpFormat_String() {
        ApplicationContext mockContext = mock(ApplicationContext.class);
        Environment mockEnv = mock(Environment.class);
        when(mockContext.getEnvironment()).thenReturn(mockEnv);

        JsonRpcController controller = new JsonRpcController(mockContext);
        String result = "simple string";

        Map<String, Object> wrapped = (Map<String, Object>) ReflectionTestUtils.invokeMethod(
                controller, "wrapInUcpFormat", "test.capability", result);

        assertNotNull(wrapped);
        assertEquals("simple string", wrapped.get("result"));
    }
}
