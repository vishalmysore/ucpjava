package io.github.vishalmysore.ucp.server.discovery;

import io.github.vishalmysore.ucp.annotation.UCPBusiness;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UCPController.class)
class UCPControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UCPController ucpController;

    @MockBean
    private ApplicationContext context;

    @Test
    void testGetUCPManifest_NoBusiness() throws Exception {
        ReflectionTestUtils.setField(ucpController, "detectedBusiness", null);
        mockMvc.perform(get("/.well-known/ucp")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUCPManifest_WithBusiness() throws Exception {
        UCPBusiness mockBusiness = new UCPBusiness() {
            @Override
            public String name() { return "Test Business"; }
            @Override
            public String version() { return "2026-01-19"; }
            @Override
            public io.github.vishalmysore.ucp.annotation.UCPCapability[] capabilities() { return new io.github.vishalmysore.ucp.annotation.UCPCapability[0]; }
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() { return UCPBusiness.class; }
        };

        ReflectionTestUtils.setField(ucpController, "detectedBusiness", mockBusiness);

        mockMvc.perform(get("/.well-known/ucp")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ucp.version").value("2026-01-19"))
                .andExpect(jsonPath("$.ucp.services['dev.ucp.shopping']").exists());
    }
}
