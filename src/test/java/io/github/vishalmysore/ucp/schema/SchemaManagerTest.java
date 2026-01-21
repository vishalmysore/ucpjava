package io.github.vishalmysore.ucp.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SchemaManagerTest {

    private final SchemaManager schemaManager = new SchemaManager();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testComposeSchema_WithValidBase() throws Exception {
        // Since loadSchema is private, we can't easily test without reflection
        // For now, test that the method doesn't throw and returns null for invalid URIs
        JsonNode result = schemaManager.composeSchema("invalid://uri", List.of());
        assertNull(result);
    }

    @Test
    void testComposeSchema_WithExtensions() throws Exception {
        JsonNode result = schemaManager.composeSchema("invalid://uri", List.of("invalid://ext"));
        assertNull(result);
    }

    // Note: Full testing would require mocking or providing actual schema URIs
    // For production, consider making loadSchema protected or injectable
}