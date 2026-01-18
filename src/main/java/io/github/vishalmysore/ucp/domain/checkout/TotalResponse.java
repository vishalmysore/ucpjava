ckage io.github.vishalmysore.ucp.domain.checkout;
import lombok.Data;

@Data
public class TotalResponse {
    public enum Type {
        subtotal, items_discount, tax, fulfillment, discount, total, fee
    }
    
    private Type type;
    private Long amount; // Minor currency units
    private String displayText;
}




