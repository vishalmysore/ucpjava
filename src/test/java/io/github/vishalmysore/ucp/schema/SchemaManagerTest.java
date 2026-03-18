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
        JsonNode baseSchema = objectMapper.readTree("{\"type\": \"object\"}");
        schemaManager.cacheSchema("base://uri", baseSchema);

        JsonNode result = schemaManager.composeSchema("base://uri", List.of());

        assertNotNull(result);
        assertTrue(result.has("allOf"));
        assertEquals(1, result.get("allOf").size());
        assertEquals("object", result.get("allOf").get(0).get("type").asText());
    }

    @Test
    void testComposeSchema_WithExtensions() throws Exception {
        JsonNode baseSchema = objectMapper.readTree("{\"type\": \"object\"}");
        JsonNode extSchema = objectMapper.readTree("{\"properties\": {\"ext\": {\"type\": \"string\"}}}");

        schemaManager.cacheSchema("base://uri", baseSchema);
        schemaManager.cacheSchema("ext://uri", extSchema);

        JsonNode result = schemaManager.composeSchema("base://uri", List.of("ext://uri"));

        assertNotNull(result);
        assertEquals(2, result.get("allOf").size());
    }

    @Test
    void testValidate() {
        SchemaManager.ValidationResult result = schemaManager.validate(new Object(), null);
        assertTrue(result.isValid());
        assertNull(result.getErrorMessage());
    }

    @Test
    void testCacheSchema() throws Exception {
        JsonNode schema = objectMapper.readTree("{\"type\": \"string\"}");
        schemaManager.cacheSchema("test://uri", schema);

        // Indirectly verify by using composeSchema
        JsonNode result = schemaManager.composeSchema("test://uri", List.of());
        assertNotNull(result);
        assertEquals("string", result.get("allOf").get(0).get("type").asText());
    }
}
