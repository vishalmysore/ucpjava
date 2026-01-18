ckage io.github.vishalmysore.ucp.annotation;

import java.lang.annotation.*;

/**
 * Designate this application as a UCP platform.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UCPPlatform {
    /**
     * Platform name
     */
    String name();
    
    /**
     * Platform UCP version (YYYY-MM-DD format)
     */
    String version();
    
    /**
     * Capabilities supported by this platform
     */
    UCPCapability[] capabilities() default {};
}




