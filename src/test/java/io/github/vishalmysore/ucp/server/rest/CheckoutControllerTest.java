package io.github.vishalmysore.ucp.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vishalmysore.ucp.domain.discovery.UCPAware;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CheckoutController.class)
class CheckoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UCPAware checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCheckout() throws Exception {
        Map<String, Object> mockResponse = Map.of("id", "test-session");
        when(checkoutService.createCheckout(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/ucp/v1/checkout-sessions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("items", List.of()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-session"));
    }

    @Test
    void testGetCheckout() throws Exception {
        Map<String, Object> mockResponse = Map.of("id", "test-session", "status", "active");
        when(checkoutService.getCheckout("test-id")).thenReturn(mockResponse);

        mockMvc.perform(get("/ucp/v1/checkout-sessions/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void testUpdateCheckout() throws Exception {
        Map<String, Object> mockResponse = Map.of("id", "test-session", "updated", true);
        when(checkoutService.updateCheckout(eq("test-id"), any())).thenReturn(mockResponse);

        mockMvc.perform(put("/ucp/v1/checkout-sessions/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("status", "updated"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated").value(true));
    }

    @Test
    void testCompleteCheckout() throws Exception {
        Map<String, Object> mockResponse = Map.of("id", "test-session", "completed", true);
        when(checkoutService.completeCheckout(eq("test-id"), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/ucp/v1/checkout-sessions/test-id/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("paymentMethod", "card"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testCancelCheckout() throws Exception {
        Map<String, Object> mockResponse = Map.of("id", "test-session", "cancelled", true);
        when(checkoutService.cancelCheckout("test-id")).thenReturn(mockResponse);

        mockMvc.perform(post("/ucp/v1/checkout-sessions/test-id/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cancelled").value(true));
    }
}