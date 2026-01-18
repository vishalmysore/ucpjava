package io.github.vishalmysore.ucp.domain.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MessageError.class, name = "error"),
    @JsonSubTypes.Type(value = MessageWarning.class, name = "warning"),
    @JsonSubTypes.Type(value = MessageInfo.class, name = "info")
})
public abstract class Message {
    private String type;
    private String code;
    private String path; // JSONPath reference
    private String contentType = "plain"; // plain or markdown
    private String content;
}

