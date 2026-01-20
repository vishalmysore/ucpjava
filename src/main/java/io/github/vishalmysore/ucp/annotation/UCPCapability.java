package io.github.vishalmysore.ucp.annotation;

import java.lang.annotation.*;

/**
 * Declare a UCP capability supported by the business or platform.
 * Capabilities use reverse-DNS naming (e.g., dev.ucp.shopping.checkout).
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(UCPCapabilities.class)
public @interface UCPCapability {
    /**
     * Capability name in reverse-DNS format
     * Examples: dev.ucp.shopping.checkout, dev.ucp.shopping.order
     */
    String name();

    /**
     * Capability version in YYYY-MM-DD format
     */
    String version();

    /**
     * Optional capability specification URI
     */
    String spec() default "";

    /**
     * Optional schema URI for this capability
     */
    String schema() default "";

    /**
     * Optional parent capability this extends
     */
    String extendsCapability() default "";

}
