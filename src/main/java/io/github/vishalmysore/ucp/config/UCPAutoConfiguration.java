package io.github.vishalmysore.ucp.config;

import io.github.vishalmysore.ucp.annotation.*;
import io.github.vishalmysore.ucp.handler.PaymentHandlerRegistry;
import io.github.vishalmysore.ucp.negotiation.CapabilityNegotiator;
import io.github.vishalmysore.ucp.schema.SchemaManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "io.github.vishalmysore.ucp")
public class UCPAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public PaymentHandlerRegistry paymentHandlerRegistry(ApplicationContext context) {
        return new PaymentHandlerRegistry(context);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public CapabilityNegotiator capabilityNegotiator() {
        return new CapabilityNegotiator();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public SchemaManager schemaManager() {
        return new SchemaManager();
    }
}




