# UCP Java - Universal Commerce Protocol Implementation

A comprehensive Java implementation of the [Universal Commerce Protocol (UCP)](https://github.com/Universal-Commerce-Protocol/ucp) for e-commerce checkout, payments, and order management.

## Features

- **Multiple Transport Bindings**: REST (primary), MCP, A2A, Embedded
- **Payment Handler Framework**: Extensible payment processing with tokenization support
- **Capability Negotiation**: Server-selects architecture with capability intersection
- **Schema-Based Validation**: Runtime schema composition with lenient validation (warnings)
- **Annotation-Driven Configuration**: `@EnableUCP`, `@UCPCapability`, `@UCPHandler`
- **Order Management**: Full order lifecycle support via `dev.ucp.shopping.order` extension
- **Spring Boot Integration**: Enterprise-grade with auto-configuration

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.vishalmysore</groupId>
    <artifactId>ucpjava</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Enable UCP in Spring Boot

```java
@SpringBootApplication
@EnableUCP
@UCPBusiness(
    name = "My Store",
    version = "2026-01-12",
    capabilities = {
        @UCPCapability(name = "dev.ucp.shopping.checkout", version = "2026-01-11"),
        @UCPCapability(name = "dev.ucp.shopping.order", version = "2026-01-11")
    }
)
public class MyStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyStoreApplication.class, args);
    }
}
```

### Create a Payment Handler

```java
@Component
@UCPHandler(
    name = "com.mystore.tokenizer",
    version = "2026-01-12",
    configSchema = "https://mystore.com/schemas/tokenizer-config.json"
)
public class MyPaymentHandler implements PaymentHandler {
    @Override
    public PaymentHandlerResponse getHandlerDeclaration() {
        // Return handler configuration
    }
    
    @Override
    public PaymentInstrument acquireInstrument(PaymentCredential credential, BindingContext binding) {
        // Process payment credential
    }
    
    @Override
    public ProcessingResult processPayment(PaymentInstrument instrument) {
        // Process the payment
    }
}
```

## Architecture

- **Domain Models**: Checkout, Payment, Order entities following UCP spec
- **Discovery**: `/.well-known/ucp` endpoint with profile negotiation
- **REST Controllers**: Full CRUD operations for checkout and order management
- **JSON-RPC Support**: MCP/A2A transport bindings
- **Schema Manager**: Runtime composition using `allOf` patterns
- **Handler Registry**: Auto-discovery of `@UCPHandler` annotated classes

## UCP Protocol Version

Implements UCP specification version: **2026-01-11**

## Documentation

- [UCP Specification](https://github.com/Universal-Commerce-Protocol/ucp/tree/main/spec)
- [API Documentation](http://localhost:8080/swagger-ui.html) (when running)

## License

MIT License - See LICENSE file
