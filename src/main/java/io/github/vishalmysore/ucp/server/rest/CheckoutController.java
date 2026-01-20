package io.github.vishalmysore.ucp.server.rest;

import io.github.vishalmysore.ucp.domain.checkout.*;
import io.github.vishalmysore.ucp.domain.common.Message;

import io.github.vishalmysore.ucp.domain.discovery.UCPAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for UCP checkout operations.
 */
@RestController
@RequestMapping("/ucp/v1")
public class CheckoutController {

    /**
     * Create a new checkout session.
     */
    @Autowired
    private UCPAware checkoutSerice;

    @PostMapping("/checkout-sessions")
    public ResponseEntity<?> createCheckout(@RequestBody Map<String, Object> request) {
        return ResponseEntity.status(201).body(checkoutSerice.createCheckout(request));
    }

    @GetMapping("/checkout-sessions/{id}")
    public ResponseEntity<?> getCheckout(@PathVariable String id) {
        return ResponseEntity.ok(checkoutSerice.getCheckout(id));
    }

    @PutMapping("/checkout-sessions/{id}")
    public ResponseEntity<?> updateCheckout(@PathVariable String id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(checkoutSerice.updateCheckout(id, request));
    }

    @PostMapping("/checkout-sessions/{id}/complete")
    public ResponseEntity<?> completeCheckout(@PathVariable String id, @RequestBody Map<String, Object> payment) {
        return ResponseEntity.ok(checkoutSerice.completeCheckout(id, payment));
    }

    @PostMapping("/checkout-sessions/{id}/cancel")
    public ResponseEntity<?> cancelCheckout(@PathVariable String id) {
        return ResponseEntity.ok(checkoutSerice.cancelCheckout(id));
    }
}
