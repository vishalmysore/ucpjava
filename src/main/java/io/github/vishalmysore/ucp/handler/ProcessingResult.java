package io.github.vishalmysore.ucp.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of payment processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingResult {
    public enum Status {
        success, failed, pending, requires_action
    }

    private Status status;
    private String transactionId;
    private String message;
    private Object data; // Additional result data
}


