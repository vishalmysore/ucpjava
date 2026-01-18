ckage io.github.vishalmysore.ucp.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageError extends Message {
    public enum Severity {
        recoverable,
        requires_buyer_input,
        requires_buyer_review
    }
    
    private Severity severity;
    
    public MessageError() {
        setType("error");
    }
}




