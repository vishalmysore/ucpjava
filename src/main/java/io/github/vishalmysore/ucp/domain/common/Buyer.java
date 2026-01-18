package io.github.vishalmysore.ucp.domain.common;
import lombok.Data;

@Data
public class Buyer {
    private String id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private PostalAddress shippingAddress;
    private PostalAddress billingAddress;
}




