package io.github.vishalmysore.ucp.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Manages UCP schema composition and validation.
 * Handles runtime merging of base schemas with extensions using allOf patterns.
 */
@Component
public class SchemaManager {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, JsonNode> schemaCache = new HashMap<>();

    /**
     * Compose a schema by merging base schema with active extensions.
     * @param baseSchemaUri Base schema URI
     * @param extensionSchemaUris Extension schema URIs
     * @return Composed schema
     */
    public JsonNode composeSchema(String baseSchemaUri, List<String> extensionSchemaUris) {
        try {
            JsonNode baseSchema = loadSchema(baseSchemaUri);
            if (baseSchema == null) {
                return null;
            }

            List<JsonNode> allOfSchemas = new ArrayList<>();
            allOfSchemas.add(baseSchema);

            for (String extensionUri : extensionSchemaUris) {
                JsonNode extensionSchema = loadSchema(extensionUri);
                if (extensionSchema != null) {
                    allOfSchemas.add(extensionSchema);
                }
            }

            // Create allOf composition
            Map<String, Object> composed = new HashMap<>();
            composed.put("allOf", allOfSchemas);

            return objectMapper.valueToTree(composed);
        } catch (Exception e) {
            // Log error
            return null;
        }
    }

    /**
     * Validate data against a schema.
     * @param data The data to validate
     * @param schema The schema
     * @return Validation result
     */
    public ValidationResult validate(Object data, JsonNode schema) {
        // TODO: Implement JSON Schema validation
        // For now, return success
        return new ValidationResult(true, null);
    }

    private JsonNode loadSchema(String schemaUri) {
        // TODO: Implement schema loading from URI or cache
        // For now, return cached or null
        return schemaCache.get(schemaUri);
    }

    /**
     * Cache a schema for reuse.
     */
    public void cacheSchema(String uri, JsonNode schema) {
        schemaCache.put(uri, schema);
    }

    /**
     * Validation result.
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}


