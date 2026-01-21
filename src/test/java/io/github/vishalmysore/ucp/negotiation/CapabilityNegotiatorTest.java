package io.github.vishalmysore.ucp.negotiation;

import io.github.vishalmysore.ucp.annotation.UCPCapability;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CapabilityNegotiatorTest {

    private final CapabilityNegotiator negotiator = new CapabilityNegotiator();

    // Mock UCPCapability for testing
    private UCPCapability createMockCapability(String name, String version) {
        return new UCPCapability() {
            @Override
            public String name() { return name; }
            @Override
            public String version() { return version; }
            @Override
            public String spec() { return ""; }
            @Override
            public String schema() { return ""; }
            @Override
            public String extendsCapability() { return ""; }
            @Override
            public Class<? extends Annotation> annotationType() { return UCPCapability.class; }
        };
    }

    @Test
    void testNegotiateCapabilities_Matching() {
        UCPCapability platformCap = createMockCapability("checkout", "1.0");
        UCPCapability businessCap = createMockCapability("checkout", "1.1");

        List<UCPCapability> platformCaps = List.of(platformCap);
        List<UCPCapability> businessCaps = List.of(businessCap);

        List<UCPCapability> result = negotiator.negotiateCapabilities(platformCaps, businessCaps);

        assertEquals(1, result.size());
        assertEquals("checkout", result.get(0).name());
        assertEquals("1.0", result.get(0).version());
    }

    @Test
    void testNegotiateCapabilities_NoMatch() {
        UCPCapability platformCap = createMockCapability("checkout", "1.0");
        UCPCapability businessCap = createMockCapability("payment", "1.0");

        List<UCPCapability> platformCaps = List.of(platformCap);
        List<UCPCapability> businessCaps = List.of(businessCap);

        List<UCPCapability> result = negotiator.negotiateCapabilities(platformCaps, businessCaps);

        assertTrue(result.isEmpty());
    }

    @Test
    void testNegotiateCapabilities_IncompatibleVersion() {
        UCPCapability platformCap = createMockCapability("checkout", "2.0");
        UCPCapability businessCap = createMockCapability("checkout", "1.0");

        List<UCPCapability> platformCaps = List.of(platformCap);
        List<UCPCapability> businessCaps = List.of(businessCap);

        List<UCPCapability> result = negotiator.negotiateCapabilities(platformCaps, businessCaps);

        assertTrue(result.isEmpty());
    }

    @Test
    void testNegotiateCapabilities_MultipleMatches() {
        UCPCapability platformCap1 = createMockCapability("checkout", "1.0");
        UCPCapability platformCap2 = createMockCapability("payment", "1.0");
        UCPCapability businessCap1 = createMockCapability("checkout", "1.1");
        UCPCapability businessCap2 = createMockCapability("payment", "1.0");

        List<UCPCapability> platformCaps = List.of(platformCap1, platformCap2);
        List<UCPCapability> businessCaps = List.of(businessCap1, businessCap2);

        List<UCPCapability> result = negotiator.negotiateCapabilities(platformCaps, businessCaps);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.name().equals("checkout")));
        assertTrue(result.stream().anyMatch(c -> c.name().equals("payment")));
    }
}