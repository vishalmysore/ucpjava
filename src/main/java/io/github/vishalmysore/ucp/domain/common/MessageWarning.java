package io.github.vishalmysore.ucp.domain.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageWarning extends Message {
    public MessageWarning() {
        setType("warning");
    }
}

