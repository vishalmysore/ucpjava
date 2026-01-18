ckage io.github.vishalmysore.ucp.annotation;

import java.lang.annotation.*;

/**
 * Designate this application as a UCP business (merchant/seller).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UCPBusiness {
    /**
     * Business name
     */
    String name();
    
    /**
     * Business UCP version (YYYY-MM-DD format)
     */
    String version();
    
    /**
     * Capabilities supported by this business
     */
    UCPCapability[] capabilities() default {};
}




