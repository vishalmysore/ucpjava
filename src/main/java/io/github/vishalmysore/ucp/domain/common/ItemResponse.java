ckage io.github.vishalmysore.ucp.domain.common;
import lombok.Data;

@Data
public class ItemResponse {
    private String id;
    private String title;
    private Long price; // Minor currency units
    private String imageUrl;
    private String description;
}




