package io.github.vishalmysore.ucp.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of UCPResult for wrapping business logic responses.
 */
public class SimpleUCPResult implements UCPResult {
    private final Map<String, String> result;

    public SimpleUCPResult(Map<String, String> result) {
        this.result = result != null ? result : new HashMap<>();
    }

    public SimpleUCPResult(String key, String value) {
        this.result = new HashMap<>();
        this.result.put(key, value);
    }

    @Override
    public Map<String, String> getResult() {
        return result;
    }
}
