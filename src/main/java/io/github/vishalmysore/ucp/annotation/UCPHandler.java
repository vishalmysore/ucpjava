package io.github.vishalmysore.ucp.annotation;

import org.springframework.stereotype.Component;
import java.lang.annotation.*;

/**
 * Register a payment handler implementation.
 * Handler names should use reverse-DNS format (e.g., com.google.pay)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface UCPHandler {
    /**
     * Payment handler name in reverse-DNS format
     * Examples: com.google.pay, com.stripe.tokenizer
     */
    String name();
    
    /**
     * Handler version in YYYY-MM-DD format
     */
    String version() default "2026-01-11";
    
    /**
     * Handler specification URI
     */
    String spec() default "";
    
    /**
     * Configuration schema URI (JSON Schema)
     */
    String configSchema() default "";
    
    /**
     * Payment instrument schema URIs (JSON Schema)
     */
    String[] instrumentSchemas() default {};
}

