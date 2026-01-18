ckage io.github.vishalmysore.ucp.domain.checkout;
import io.github.vishalmysore.ucp.domain.common.ItemResponse;
import lombok.Data;
import java.util.List;

@Data
public class LineItemResponse {
    private String id;
    private ItemResponse item;
    private Integer quantity;
    private List<TotalResponse> totals;
    private String parentId; // For hierarchical items
}




