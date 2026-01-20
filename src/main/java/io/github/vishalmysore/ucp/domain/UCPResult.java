package io.github.vishalmysore.ucp.domain;

import io.github.vishalmysore.common.CommonClientResponse;
import io.github.vishalmysore.mcp.domain.CallToolResult;

import java.util.Map;

public interface UCPResult extends CommonClientResponse {
    Map<String,String> getResult();
}
