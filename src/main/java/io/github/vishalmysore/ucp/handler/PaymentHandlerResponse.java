package io.github.vishalmysore.ucp.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response containing payment handler declaration.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHandlerResponse {
    private String name;
    private String version;
    private String spec;
    private String configSchema;
    private String[] instrumentSchemas;
    private Object config; // Handler-specific configuration
}


