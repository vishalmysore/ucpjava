ckage io.github.vishalmysore.ucp.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment credential provided by the platform.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCredential {
    private String type; // e.g., "card", "token"
    private Object data; // Credential data (should be tokenized/minimal)
    private String schema; // Schema URI for validation
}


