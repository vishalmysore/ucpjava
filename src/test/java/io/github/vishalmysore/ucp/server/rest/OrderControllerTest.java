package io.github.vishalmysore.ucp.server.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateOrder() throws Exception {
        Map<String, Object> request = Map.of("items", List.of(Map.of("id", "item1")));

        mockMvc.perform(post("/ucp/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2026-01-11"));
    }

    @Test
    void testGetOrder() throws Exception {
        mockMvc.perform(get("/ucp/order/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2026-01-11"));
    }

    @Test
    void testUpdateOrder() throws Exception {
        Map<String, Object> updates = Map.of("status", "shipped");

        mockMvc.perform(patch("/ucp/order/123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("2026-01-11"));
    }
}