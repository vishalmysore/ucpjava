package io.github.vishalmysore.ucp.domain.discovery;

import io.github.vishalmysore.ucp.annotation.UCPCapability;
import java.util.Map;

/**
 * Interface defining standard UCP capabilities that businesses can implement.
 * All methods follow UCP specification requirements and use proper parameter naming.
 */
public interface UCPAware {

    /**
     * Creates a new checkout session with line items, currency, and payment configuration.
     * Follows the UCP checkout specification for request/response format.
     */
    @UCPCapability(
            name = "dev.ucp.shopping.checkout",
            version = "2026-01-11",
            spec = "https://ucp.dev/specification/checkout",
            schema = "https://ucp.dev/schemas/shopping/checkout.json"
    )
    public Object createCheckout(Map<String, Object> checkoutRequest);

    /**
     * Retrieves an existing checkout session by its unique identifier.
     */
    @UCPCapability(name = "dev.ucp.shopping.checkout", version = "2026-01-11")
    public Object getCheckout(String checkoutId);

    /**
     * Updates an existing checkout session with new line items, shipping, or payment details.
     */
    @UCPCapability(name = "dev.ucp.shopping.checkout", version = "2026-01-11")
    public Object updateCheckout(String checkoutId, Map<String, Object> checkoutUpdate);

    /**
     * Completes the checkout and places the order using the provided payment details.
     * Includes idempotency key to prevent duplicate order creation.
     */
    @UCPCapability(name = "dev.ucp.shopping.checkout", version = "2026-01-11")
    public Object completeCheckout(String checkoutId, Map<String, Object> paymentDetails);

    /**
     * Cancels a pending checkout session.
     * Includes idempotency key to ensure safe cancellation.
     */
    @UCPCapability(name = "dev.ucp.shopping.checkout", version = "2026-01-11")
    public Object cancelCheckout(String checkoutId);

    /**
     * Links a user's platform identity with their business account using OAuth 2.0.
     * Handles authorization code exchange and token management.
     */
    @UCPCapability(
            name = "dev.ucp.common.identity_linking",
            version = "2026-01-11",
            spec = "https://ucp.dev/specification/identity-linking",
            schema = "https://ucp.dev/schemas/common/identity_linking.json"
    )
    public Object linkIdentity(Map<String, Object> oauthRequest);

    /**
     * Retrieves order details including line items, fulfillment status, and totals.
     * Returns the complete order state with immutable line items.
     */
    @UCPCapability(
            name = "dev.ucp.shopping.order",
            version = "2026-01-11",
            spec = "https://ucp.dev/specification/order",
            schema = "https://ucp.dev/schemas/shopping/order.json"
    )
    public Object getOrder(String orderId);
}