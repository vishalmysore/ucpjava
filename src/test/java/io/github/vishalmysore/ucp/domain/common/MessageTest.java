package io.github.vishalmysore.ucp.domain.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMessageErrorSerialization() throws Exception {
        MessageError error = new MessageError();
        error.setCode("ERR001");
        error.setContent("An error occurred");
        error.setSeverity(MessageError.Severity.recoverable);

        String json = objectMapper.writeValueAsString(error);
        assertTrue(json.contains("\"type\":\"error\""));
        assertTrue(json.contains("\"severity\":\"recoverable\""));

        Message deserialized = objectMapper.readValue(json, Message.class);
        assertTrue(deserialized instanceof MessageError);
        assertEquals("ERR001", deserialized.getCode());
        assertEquals(MessageError.Severity.recoverable, ((MessageError) deserialized).getSeverity());
    }

    @Test
    void testMessageWarningSerialization() throws Exception {
        MessageWarning warning = new MessageWarning();
        warning.setCode("WARN001");
        warning.setContent("A warning occurred");

        String json = objectMapper.writeValueAsString(warning);
        assertTrue(json.contains("\"type\":\"warning\""));

        Message deserialized = objectMapper.readValue(json, Message.class);
        assertTrue(deserialized instanceof MessageWarning);
        assertEquals("WARN001", deserialized.getCode());
    }

    @Test
    void testMessageInfoSerialization() throws Exception {
        MessageInfo info = new MessageInfo();
        info.setCode("INFO001");
        info.setContent("Information message");

        String json = objectMapper.writeValueAsString(info);
        assertTrue(json.contains("\"type\":\"info\""));

        Message deserialized = objectMapper.readValue(json, Message.class);
        assertTrue(deserialized instanceof MessageInfo);
        assertEquals("INFO001", deserialized.getCode());
    }
}
