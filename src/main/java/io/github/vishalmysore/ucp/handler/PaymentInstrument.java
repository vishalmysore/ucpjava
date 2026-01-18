package io.github.vishalmysore.ucp.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a payment instrument that can be used for processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInstrument {
    private String id;
    private String type; // e.g., "credit_card", "token"
    private String provider; // e.g., "stripe", "paypal"
    private Object data; // Instrument-specific data (tokenized)
    private String schema; // Schema URI for validation
}


