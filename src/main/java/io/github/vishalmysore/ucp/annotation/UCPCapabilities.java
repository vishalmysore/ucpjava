package io.github.vishalmysore.ucp.annotation;

import java.lang.annotation.*;

/**
 * Container annotation for multiple @UCPCapability declarations
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UCPCapabilities {
    UCPCapability[] value();
}




