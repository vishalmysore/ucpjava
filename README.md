# UCP Java - Universal Commerce Protocol Implementation
Pure Java implementation of [Universal Commerce Protocol (UCP)](https://github.com/Universal-Commerce-Protocol/ucp) for e-commerce checkout, payments, and order management.
UCP is a new open standard / protocol for AI-driven shopping and commerce, announced by Google in January 2026. It’s designed to let AI agents (e.g., Google Gemini, Search-AI Mode, other assistants) interact with retailers, payment systems, identity systems, and backend commerce systems in a unified way — without requiring separate custom integrations for every agent, merchant, and platform.
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

## Usage

  /**
     * 
     * Request is like this
     * 
     * {
     * "jsonrpc": "2.0",
     * "method": "whatThisPersonFavFood",
     * "params": {
     * "_meta": {
     * "ucp": {
     * "profile": "https://platform.example/profiles/v2026-01/shopping-agent.json"
     * }
     * },
     * "provideAllValuesInPlainEnglish": "vishal is coming home what should i cook"
     * },
     * "id": 17
     * }
     * 
     * response is like this
     * {
     * "jsonrpc": "2.0",
     * "id": 17,
     * "result": {
     * "ucp": {
     * "version": "2026-01-11",
     * "capabilities": [
     * {
     * "name": "io.github.vishalmysore.car_booking",
     * "version": "2026-01-19"
     * }
     * ]
     * },
     * "booking_id": "bk_1234567890",
     * "status": "confirmed",
     * "car_model": "Toyota Camry",
     * "booking_date": "2026-01-25"
     * }
     * }
     * 
     * @param request
     * @param httpRequest
     * @return
     * @throws AIProcessingException
     */