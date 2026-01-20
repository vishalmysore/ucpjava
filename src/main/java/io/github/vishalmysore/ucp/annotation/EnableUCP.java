package io.github.vishalmysore.ucp.annotation;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * Enable UCP protocol support in Spring Boot application.
 * This annotation triggers auto-configuration for UCP components including
 * discovery endpoints, capability negotiation, payment handler registry, etc.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableUCP {
    /**
     * Enable REST transport binding (default: true)
     */
    boolean restEnabled() default true;
    
    /**
     * Enable MCP transport binding (default: false)
     */
    boolean mcpEnabled() default false;
    
    /**
     * Enable A2A transport binding (default: false)
     */
    boolean a2aEnabled() default false;
    
    /**
     * Enable embedded transport binding (default: false)
     */
    boolean embeddedEnabled() default false;
}




