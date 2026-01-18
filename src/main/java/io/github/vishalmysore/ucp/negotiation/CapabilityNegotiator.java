ckage io.github.vishalmysore.ucp.negotiation;

import io.github.vishalmysore.ucp.annotation.UCPCapability;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Handles UCP capability negotiation between platform and business.
 */
@Component
public class CapabilityNegotiator {

    /**
     * Negotiate capabilities between platform and business profiles.
     * @param platformCapabilities Platform's declared capabilities
     * @param businessCapabilities Business's declared capabilities
     * @return Intersected capabilities
     */
    public List<UCPCapability> negotiateCapabilities(List<UCPCapability> platformCapabilities,
                                                   List<UCPCapability> businessCapabilities) {
        List<UCPCapability> negotiated = new ArrayList<>();

        for (UCPCapability platformCap : platformCapabilities) {
            for (UCPCapability businessCap : businessCapabilities) {
                if (matches(platformCap, businessCap)) {
                    negotiated.add(platformCap); // Use platform's version
                    break;
                }
            }
        }

        return negotiated;
    }

    private boolean matches(UCPCapability cap1, UCPCapability cap2) {
        return cap1.name().equals(cap2.name()) &&
               isCompatibleVersion(cap1.version(), cap2.version());
    }

    private boolean isCompatibleVersion(String version1, String version2) {
        // Simple version comparison - in practice, use proper semver or date comparison
        return version1.compareTo(version2) <= 0; // platform <= business
    }

    /**
     * Fetch platform profile from URI.
     * @param profileUri The profile URI
     * @return Parsed profile
     */
    public UCPProfile fetchPlatformProfile(String profileUri) {
        // TODO: Implement HTTP fetch and JSON parsing
        // For now, return empty profile
        return new UCPProfile();
    }

    /**
     * Represents a UCP profile.
     */
    public static class UCPProfile {
        private String version;
        private List<UCPCapability> capabilities = new ArrayList<>();
        private Map<String, Object> services = new HashMap<>();
        private Map<String, Object> payment = new HashMap<>();
        private Map<String, Object> signingKeys = new HashMap<>();

        // Getters and setters
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public List<UCPCapability> getCapabilities() { return capabilities; }
        public void setCapabilities(List<UCPCapability> capabilities) { this.capabilities = capabilities; }

        public Map<String, Object> getServices() { return services; }
        public void setServices(Map<String, Object> services) { this.services = services; }

        public Map<String, Object> getPayment() { return payment; }
        public void setPayment(Map<String, Object> getPayment) { this.payment = getPayment; }

        public Map<String, Object> getSigningKeys() { return signingKeys; }
        public void setSigningKeys(Map<String, Object> signingKeys) { this.signingKeys = signingKeys; }
    }
}


