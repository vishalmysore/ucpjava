package io.github.vishalmysore.ucp.domain.common;
import lombok.Data;

@Data
public class PostalAddress {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String postalCode;
    private String countryCode; // ISO 3166-1 alpha-2
}

