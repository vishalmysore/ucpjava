package io.github.vishalmysore.ucp.domain.common;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private String rel;
    private String href;
    private String title;
}

