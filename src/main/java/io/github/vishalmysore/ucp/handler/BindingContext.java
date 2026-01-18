ckage io.github.vishalmysore.ucp.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Context information for the binding/transport.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingContext {
    private String transport; // "rest", "mcp", "a2a", "embedded"
    private String platformProfileUri;
    private Object metadata; // Transport-specific metadata
}


