# Universal Commerce Protocol (UCP) in Java: Complete Guide to Agentic Checkout

### Learn how to implement UCP capabilities in a Spring Boot app and create an agentic shopping application

Pure Java implementation of [Universal Commerce Protocol (UCP)](https://github.com/Universal-Commerce-Protocol/ucp) for e-commerce checkout, payments, and order management.

As AI agents and autonomous commerce become increasingly prevalent, the need for standardized interoperability has never been more critical. This comprehensive guide demonstrates how to implement UCP (Universal Commerce Protocol) in Java using the tools4ai framework, providing both theoretical understanding and practical implementation patterns.
## Features

- **Multiple Transport Bindings**: REST (primary), MCP, A2A, Embedded
- **Payment Handler Framework**: Extensible payment processing with tokenization support
- **Capability Negotiation**: Server-selects architecture with capability intersection
- **Schema-Based Validation**: Runtime schema composition with lenient validation (warnings)
- **Annotation-Driven Configuration**: `@EnableUCP`, `@UCPCapability`, `@UCPHandler`
- **Order Management**: Full order lifecycle support via `dev.ucp.shopping.order` extension
- **Spring Boot Integration**: Enterprise-grade with auto-configuration

## What is UCP?

UCP (Universal Commerce Protocol) is an emerging agent-to-agent interoperability protocol designed to let AI agents built by different vendors communicate, discover capabilities, exchange context, and invoke actions in a standardized way. It focuses on capability discovery, identity, security, and structured message exchange, enabling agents to collaborate without tight coupling or proprietary integrations.

In practice, UCP acts as a neutral "language" between agents, enabling ecosystems where tools, agents, and services can work together across platforms—similar in spirit to how HTTP standardized web communication, but specifically for autonomous and semi-autonomous AI agents.

## Core Libraries

This implementation uses three core libraries:

- [tools4ai](https://github.com/vishalmysore/Tools4AI) - Core framework for AI agent tools
- [a2ajava](https://github.com/vishalmysore/a2ajava) - Agent-to-agent communication
- [ucpjava](https://github.com/vishalmysore/ucpjava) - UCP protocol implementation

## Why UCP Matters

Today Commerce ecosystem, platforms, businesses, payment providers, and credential providers operate on disparate systems, creating integration complexity and abandoned transactions. UCP addresses this fragmentation by providing:

- Standardized Common Language: A unified way for all commerce entities to communicate
- Functional Primitives: Core building blocks that enable seamless interactions
- Reduced Integration Complexity: No need for custom integrations for each platform
- Future-Proof Architecture: Support for both human-in-the-loop and autonomous AI agent commerce flows

## Core Concepts

### UCP Protocol Architecture

UCP addresses fragmented commerce by providing standardized interaction patterns through three key capabilities:

- Discovery: Platforms dynamically find business capabilities via profiles at /.well-known/ucp
- Transport Agnostic: Works across REST, MCP (Model Context Protocol), and A2A protocols
- Modular Design: Composable capabilities and extensions for flexible implementation

### UCP Business

A UCP Business is the entity selling goods or services that acts as the Merchant of Record (MoR), retaining financial liability and ownership. In code, this is represented by the @UCPBusiness annotation.

Key Constraint: UCP enforces that each host represents exactly one merchant to maintain clear liability and ownership.

```java
@UCPBusiness(name = "AutoGroup North", version = "2026-01-19")
public class ShoppingService implements UCPAware {
// Business implementation
}
```

### UCP Capability

A UCP Capability is a standalone core feature that a business supports—the fundamental "verbs" of UCP. These are declared using @UCPCapability annotations and follow reverse-domain naming conventions.

#### Capability Namespace Patterns

| Namespace Pattern | Authority | Example |
| --- | --- | --- |
| dev.ucp.* | UCP governing body | dev.ucp.shopping.checkout |
| com.vendor.* | Vendor organization | com.example.payments.installments |
| io.github.* | Custom implementations | io.github.vishalmysore.car_booking |

#### Standard UCP Capabilities

UCP defines several standard capabilities:

- Checkout: Cart management and checkout sessions
- Identity Linking: OAuth 2.0 authorization
- Order: Lifecycle event webhooks

#### Capability Structure

All capabilities must include:

- Name: Reverse-domain format (e.g., dev.ucp.shopping.checkout)
- Version: YYYY-MM-DD format
- Spec URL: Link to specification document
- Schema URL: Link to JSON Schema definition

```java
@UCPCapability(
name = "dev.ucp.shopping.checkout",
version = "2026-01-11",
spec = "https://ucp.dev/specification/checkout",
schema = "https://ucp.dev/schemas/shopping/checkout.json"
)
public Object createCheckout(Map<String, Object> checkoutRequest);
```

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

## Standard UCP Capabilities

### UCPAware Interface

The UCPAware interface defines standard UCP capabilities that must be implemented for core commerce operations:

```java
public interface UCPAware {

    @UCPCapability(
        name = "dev.ucp.shopping.checkout",
        version = "2026-01-11",
        spec = "https://ucp.dev/specification/checkout",
        schema = "https://ucp.dev/schemas/shopping/checkout.json"
    )
    Object createCheckout(Map<String, Object> checkoutRequest);

    @UCPCapability(
        name = "dev.ucp.shopping.order",
        version = "2026-01-11",
        spec = "https://ucp.dev/specification/order",
        schema = "https://ucp.dev/schemas/shopping/order.json"
    )
    Object getOrder(String orderId);

    @UCPCapability(
        name = "dev.ucp.common.identity_linking",
        version = "2026-01-11",
        spec = "https://ucp.dev/specification/identity-linking",
        schema = "https://ucp.dev/schemas/common/identity_linking.json"
    )
    Object linkIdentity(Map<String, Object> identityRequest);
}
```

#### Key Requirements

- Standard capabilities require exact REST endpoint implementations
- All capabilities must use YYYY-MM-DD versioning
- Spec and schema URLs must be included
- Capabilities are discovered automatically through annotation scanning

### Custom Capabilities

#### Implementation Example: Car Booking

Custom capabilities allow businesses to extend UCP with domain-specific features while maintaining protocol compliance.

```java
package io.github.vishalmysore.controllers;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.processor.ProcessorAware;
import io.github.vishalmysore.a2ui.A2UIAware;
import io.github.vishalmysore.ucp.annotation.UCPCapability;
import io.github.vishalmysore.ucp.domain.SimpleUCPResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
* UCP Business for car booking
*
* IMPORTANT CONSTRAINTS:
* - Only one agent can have tag @UCPBusiness in our Spring Boot application
* - UCP Capabilities can only be in REST controllers (not services)
* - This is required because UCP mandates support for both REST and MCP transports
* - If it's a REST controller, the UCP manifest adds transport as "rest"
    */
    @Agent(
    groupName = "carbooking",
    groupDescription = "car booking service",
    prompt = "You are a car booking assistant. Help users book cars based on their preferences and requirements."
    )
    @RestController(value = "/carbooking")
    public class CarbookingController implements ProcessorAware, A2UIAware {

@Action(description = "Book a car based on user preferences")
@UCPCapability(
name = "io.github.vishalmysore.car_booking",
version = "2026-01-19",
spec = "https://autogroup-north.com/specs/car-booking",
schema = "https://autogroup-north.com/schemas/booking.json"
)
@PostMapping("/bookCar")
public SimpleUCPResult bookCar(
String carType,
String pickupLocation,
String dropoffLocation,
String pickupDate,
String dropoffDate) {

     // Booking logic here
     Map<String, String> bookingDetails = new HashMap<>();
     bookingDetails.put("carType", carType);
     bookingDetails.put("pickupLocation", pickupLocation);
     bookingDetails.put("dropoffLocation", dropoffLocation);
     bookingDetails.put("pickupDate", pickupDate);
     bookingDetails.put("dropoffDate", dropoffDate);
     bookingDetails.put("confirmationNumber", "ABC123XYZ");

     return new SimpleUCPResult(bookingDetails);
}
}
```

#### Custom Capability Analysis

- ✅ **Correct Structure**
  - Controller Requirements: The controller properly follows the UCP pattern where capabilities must be in REST controllers to support REST transport
  - Capability Annotation: Uses proper custom namespace and date-based versioning:

```java
@UCPCapability(
name = "io.github.vishalmysore.car_booking",
version = "2026-01-19",
spec = "https://autogroup-north.com/specs/car-booking",
schema = "https://autogroup-north.com/schemas/booking.json"
)
```

  - REST Endpoint: Properly mapped with @PostMapping("/bookCar") for the booking operation
  - Return Type: Uses SimpleUCPResult which includes UCP metadata when used with MCP transport

- 📝 **Discovery Profile Impact**
  This controller will be automatically discovered and added to the discovery profile:

```json
{
"name": "io.github.vishalmysore.car_booking",
"version": "2026-01-19",
"spec": "https://autogroup-north.com/specs/car-booking",
"schema": "https://autogroup-north.com/schemas/booking.json"
}
```

#### Important Notes

- The @Agent annotation makes this available via MCP and A2A transports in addition to REST
- Custom capabilities can define their own endpoint patterns (unlike standard UCP capabilities)
- The SimpleUCPResult return type should include UCP metadata when used with MCP transport
- The UCPController will automatically register this capability when scanning for annotated methods

## Service Types & Patterns

### Service Type Comparison

| Service Type | Annotation | Transport | Use Case |
| --- | --- | --- |
| UCP Business | @UCPBusiness + @RestController | REST + MCP | Standard commerce capabilities |
| Agentic Service | @Agent | MCP + A2A | AI agent interactions |

### Agentic Service Example: Car Selling

Services that are NOT UCP capabilities but are available for AI agent interactions:

```java
package io.github.vishalmysore.service;

import com.t4a.annotations.Action;
import com.t4a.annotations.Agent;
import com.t4a.processor.ProcessorAware;
import io.github.vishalmysore.a2ui.A2UIAware;

/**
* MCP Service for car selling
*
* IMPORTANT CONSTRAINTS:
* - This is NOT a UCP Business or UCP Capability
* - Only exposed via MCP and A2A protocols
* - Does NOT require REST endpoint
* - Cannot use @UCPCapability annotation (will throw error during startup)
    */
    @Agent(
    groupName = "carbooking",
    groupDescription = "car booking service",
    prompt = "You are a car booking assistant. Help users book cars based on their preferences and requirements."
    )
    public class CarSellingService implements ProcessorAware, A2UIAware {

/**
    * This is an agentic action exposed only via MCP and A2A
    * It is NOT a UCP business or capability
      */
      @Action(description = "Sell a car based on user preferences")
      // @UCPCapability(name = "io.github.vishalmysore.sell_car", ...)
      // ⚠️ CANNOT uncomment above - will throw error:
      // "UCP Violation: Agent CarSellingService must be annotated with
      // @RestController to support REST transport"
      public Object sellCar(
      String carType,
      String pickupLocation,
      String dropoffLocation,
      String pickupDate,
      String dropoffDate) {

      return "Car sold: " + carType + " from " + pickupLocation +
      " to " + dropoffLocation + " between " + pickupDate +
      " and " + dropoffDate;
      }
      }
```

#### Pattern Guidelines

- For CarSellingService (Agentic Pattern):
  - Uses @Agent annotation for agent discovery
  - @Action methods exposed via MCP/A2A only
  - No REST endpoints required
  - Keep @UCPCapability commented out - this is not a UCP business
  - The sellCar method will be available through MCP at /ucp/mcp and A2A protocols

- For UCP Standard Capabilities:
  - Use ShoppingService with UCPAware interface 
  - Must be @RestController
  - Supports both REST and MCP transports

This separation allows you to support both standard UCP commerce and custom agentic functionality in the same application.

## Discovery & Endpoint Resolution

### Generated UCP Manifest

The framework automatically generates a complete UCP discovery profile at /.well-known/ucp:

```json
{
"ucp": {
"capabilities": [
{
"schema": "https://autogroup-north.com/schemas/inventory_search.json",
"name": "io.github.vishalmysore.inventory_search",
"version": "2026-01-19",
"spec": "https://autogroup-north.com/specs/inventory-search"
},
{
"schema": "https://ucp.dev/schemas/shopping/order.json",
"name": "dev.ucp.shopping.order",
"version": "2026-01-11",
"spec": "https://ucp.dev/specification/order"
},
{
"schema": "https://ucp.dev/schemas/shopping/checkout.json",
"name": "dev.ucp.shopping.checkout",
"version": "2026-01-11",
"spec": "https://ucp.dev/specification/checkout"
},
{
"schema": "https://autogroup-north.com/schemas/comparison.json",
"name": "io.github.vishalmysore.car_comparison",
"version": "2026-01-19",
"spec": "https://autogroup-north.com/specs/car-comparison"
},
{
"schema": "https://autogroup-north.com/schemas/booking.json",
"name": "io.github.vishalmysore.car_booking",
"version": "2026-01-19",
"spec": "https://autogroup-north.com/specs/car-booking"
},
{
"schema": "https://ucp.dev/schemas/common/identity_linking.json",
"name": "dev.ucp.common.identity_linking",
"version": "2026-01-11",
"spec": "https://ucp.dev/specification/identity-linking"
}
],
"services": {
"dev.ucp.shopping": {
"rest": {
"schema": "https://ucp.dev/services/shopping/openapi.json",
"endpoint": "http://localhost:7860/ucp/v1"
},
"mcp": {
"schema": "https://ucp.dev/services/shopping/mcp.openrpc.json",
"endpoint": "http://localhost:7860/ucp/mcp"
},
"version": "2026-01-11",
"spec": "https://ucp.dev/specification/overview"
}
},
"version": "2026-01-19"
}
}
```

### Client Discovery Process

To discover the specific REST call for booking a car, the client follows this systematic **Endpoint Resolution** process:

#### Step 1: Identify the Vertical (Service)

The client examines the `services` object. "Booking" or "Checkout" typically falls under the `dev.ucp.shopping` service.

- **Service Name**: `dev.ucp.shopping`
- **Base Endpoint**: `http://localhost:7860/ucp/v1`

#### Step 2: Fetch the Transport Schema

The client retrieves the OpenAPI specification from the `rest.schema` field:

```
https://ucp.dev/services/shopping/openapi.json
```

**Note**: This OpenAPI file is "thin"—it contains paths and method names (e.g., `/checkout-sessions`) but does not define the full payload structure internally.

#### Step 3: Match the Capability to the Path

The client sees the `io.github.vishalmysore.car_booking` capability is advertised. By consulting the spec or standard mapping for the shopping service, it identifies the relevant REST path.

For a booking/checkout flow, the standard path is typically:

```
POST /checkout-sessions
```

#### Step 4: Resolve the Final URL

The client appends the OpenAPI path to the service endpoint:

- Base: http://localhost:7860/ucp/v1
- Path: /checkout-sessions
- Resolved URL: http://localhost:7860/ucp/v1/checkout-sessions

#### Step 5: Construct the Payload (Schema Composition)

This is the most critical step for custom capabilities:

- Fetch Base Schema: Download the standard checkout schema from https://ucp.dev/schemas/shopping/checkout.json
- Fetch Extension Schema: Download the custom schema from https://autogroup-north.com/schemas/booking.json
- Compose: Using allOf, merge these schemas. The request body will include standard checkout fields PLUS car-specific booking fields

### Discovery Summary Table

| Step | Action | Result |
| --- | --- | --- |
| 1 | Find Service for Shopping | dev.ucp.shopping |
| 2 | Get Base Endpoint | http://localhost:7860/ucp/v1 |
| 3 | Find Path for Booking | POST /checkout-sessions |
| 4 | Get Schema for Payload | https://autogroup-north.com/schemas/booking.json |
| 5 | Compose and Send Request | Complete checkout with car booking data |

### Schema Composition

#### Extension Schema Pattern

Based on the UCP specification's Extension Schema Pattern, custom schemas act as modules that augment base commerce types. They use allOf to "plug in" to standard UCP schemas.

#### Custom Booking Schema Example

Following the naming convention {capability-name}.{TypeName}:

```json
{
"$schema": "https://json-schema.org/draft/2020-12/schema",
"$id": "https://autogroup-north.com/schemas/booking.json",
"title": "Car Booking Extension",
"description": "Extends UCP Checkout to support automotive-specific booking data.",
"$defs": {
"car_details": {
"type": "object",
"properties": {
"vin": {
"type": "string",
"pattern": "^[A-HJ-NPR-Z0-9]{17}$"
},
"make": { "type": "string" },
"model": { "type": "string" },
"year": { "type": "integer" },
"trim": { "type": "string" }
},
"required": ["vin", "make", "model"]
},
"booking_metadata": {
"type": "object",
"properties": {
"pickup_date": {
"type": "string",
"format": "date-time"
},
"dealership_id": { "type": "string" },
"test_drive_requested": {
"type": "boolean",
"default": false
}
}
},
"io.github.vishalmysore.car_booking.checkout": {
"description": "The composed checkout object for car bookings",
"allOf": [
{
"$ref": "https://ucp.dev/schemas/shopping/checkout.json"
},
{
"type": "object",
"properties": {
"car_info": {
"$ref": "#/$defs/car_details"
},
"booking_details": {
"$ref": "#/$defs/booking_metadata"
}
}
}
]
}
}
}
```

#### Key Structural Features

- Namespace Alignment: The primary composed type is named io.github.vishalmysore.car_booking.checkout, ensuring clients know which object definition to use for POST /checkout-sessions
- Composition (allOf): Explicitly references the official UCP checkout schema as a base, requiring support for standard fields while adding car-specific fields
- Self-Describing: Declares the types it introduces per UCP requirements

#### Final Request Structure

When the platform merges these schemas, the JSON payload sent to http://localhost:7860/ucp/v1/checkout-sessions looks like:

```json
{
"ucp": {
"version": "2026-01-11",
"capabilities": [
{
"name": "dev.ucp.shopping.checkout",
"version": "2026-01-11"
},
{
"name": "io.github.vishalmysore.car_booking",
"version": "2026-01-19"
}
]
},
"cart": {
"line_items": [
{
"sku": "SERVICE-FEE",
"quantity": 1
}
]
},
"car_info": {
"vin": "1HGCM82635A000001",
"make": "Honda",
"model": "Accord"
},
"booking_details": {
"pickup_date": "2026-02-01T10:00:00Z",
"dealership_id": "NORTH-001"
}
}
```

### Linking Schema in Profile

To make the schema discoverable, update the /.well-known/ucp profile:

```json
"services": {
"dev.ucp.shopping": {
"rest": {
"schema": "http://localhost:7860/v3/api-docs",
"endpoint": "http://localhost:7860/ucp/v1"
}
}
}
```

### Client Discovery with Schema

When a user says "I want to book an SUV in London," the client:

1. **Reads the Profile**: Sees support for `io.github.vishalmysore.car_booking`
2. **Fetches the Schema**: Downloads JSON from `http://localhost:7860/v3/api-docs`
3. **Finds the Match**: Searches paths for an operationId or tag related to "booking"
4. **Inspects Parameters**: Identifies required parameters:
   - `carType`
   - `pickupLocation`
   - `dropoffLocation`
   - `pickupDate`
   - `dropoffDate`
5. **Executes**: Calls `POST http://localhost:7860/bookCar?carType=SUV&pickupLocation=London...`

### Pro Tip: Request Body vs Query Parameters

**Current State**: Parameters are defined in the query:

```
POST http://localhost:7860/bookCar?carType=...&pickupLocation=...
```

**UCP Recommended**: For POST operations that create booking intents, data should be in the Request Body rather than the URL string. This is cleaner and prevents long, messy URLs.

## Tool Usage & API Calls

### MCP Tool Discovery

List all available tools using the MCP tools/list method:

```bash
curl -H "Content-Type: application/json" -d '{
"jsonrpc": "2.0",
"method": "tools/list",
"params": {},
"id": 1
}' http://localhost:7860/
```

#### Example Response

```json
{
"result": {
"_meta": {},
"tools": [
{
"parameters": null,
"inputSchema": {
"type": "object",
"properties": {
"provideAllValuesInPlainEnglish": {
"type": "string",
"description": "{\n    \"parameters\": {\n        \"carType\": \"\",\n        \"pickupLocation\": \"\",\n        \"dropoffLocation\": \"\",\n        \"pickupDate\": \"\",\n        \"dropoffDate\": \"\"\n    }\n}"
}
},
"required": ["provideAllValuesInPlainEnglish"]
},
"annotations": {
"properties": {
"usage": "To reserve a car for a specific period and location.",
"name": "Car Booking Tool",
"description": "This tool allows users to book a car by specifying the car type, pickup location, dropoff location, and pickup and dropoff dates.",
"parameters": "carType, pickupLocation, dropoffLocation, pickupDate, dropoffDate"
}
},
"description": "Book a car based on user preferences",
"name": "bookCar",
"type": null
},
{
"description": "compare 2 cars",
"name": "compareCar",
"annotations": {
"properties": {
"name": "Car Comparison Tool",
"description": "This tool compares two cars based on various parameters to help users make informed decisions."
}
}
}
]
},
"id": 1,
"jsonrpc": "2.0"
}
```

### Normal MCP Tool Calling

Execute a tool using the MCP tools/call method:

```bash
curl -H "Content-Type: application/json" -d '{
"jsonrpc": "2.0",
"id": 2,
"method": "tools/call",
"params": {
"name": "compareCar",
"arguments": {
"provideAllValuesInPlainEnglish": "{\"car1\": \"Tesla Model 3\", \"car2\": \"BMW i4\"}"
}
}
}' http://localhost:7860/
```

#### Example Response

```json
{
"result": {
"content": [
{
"type": "text",
"annotations": null,
"text": "Tesla Model 3 is better than BMW i4"
}
],
"textResult": "Tesla Model 3 is better than BMW i4"
},
"id": 2,
"jsonrpc": "2.0"
}
```

### UCP Request via MCP

For UCP-compliant requests, use the capability name as the method:

```bash
curl -H "Content-Type: application/json" -d '{
"jsonrpc": "2.0",
"method": "io.github.vishalmysore.car_booking",
"params": {
"_meta": {
"ucp": {
"profile": "https://platform.example/profiles/v2026-01/shopping-agent.json"
}
},
"car_model": "Toyota Camry",
"booking_date": "2026-01-25",
"customer_info": {
"name": "John Doe",
"email": "john@example.com"
}
},
"id": 1
}' http://localhost:7860/ucp/mcp
```

#### Example Response

```json
{
"result": {
"ucp": {
"capabilities": [
{
"name": "io.github.vishalmysore.car_booking",
"version": "2026-01-19"
}
],
"version": "2026-01-11"
},
"carType": "Toyota Camry",
"confirmationNumber": "ABC123XYZ",
"dropoffDate": null,
"dropoffLocation": null,
"pickupDate": "2026-01-25",
"pickupLocation": null
},
"id": 1,
"jsonrpc": "2.0"
}
```

## Architecture

- **Domain Models**: Checkout, Payment, Order entities following UCP spec
- **Discovery**: `/.well-known/ucp` endpoint with profile negotiation
- **REST Controllers**: Full CRUD operations for checkout and order management
- **JSON-RPC Support**: MCP/A2A transport bindings
- **Schema Manager**: Runtime composition using `allOf` patterns
- **Handler Registry**: Auto-discovery of `@UCPHandler` annotated classes

## Implementation Notes

The CarbookingController properly:

- Exposes the custom capability via both REST and MCP transports
- Returns UCP-compliant responses with proper metadata
- Handles booking parameters and returns confirmation details

Note: The null values for dropoffDate, dropoffLocation, and pickupLocation suggest the implementation may need to handle additional location parameters from the request.

## Response Compliance

The MCP server correctly formats responses according to UCP specification

This response structure is production-ready

The booking capability will be automatically discovered in the UCP profile at /.well-known/ucp

Both REST and MCP transports are fully functional for the custom capability

## Design Considerations

### Annotation-Driven Approach

The annotation-driven approach in this UCP implementation provides automatic capability discovery, clean separation of concerns, and dual transport support.

#### Advantages

1. **Automatic Discovery**
   The UCPController automatically scans for @UCPCapability annotations and generates the discovery profile. This eliminates manual configuration and ensures the profile is always in sync with code.
2. **Clean Architecture**
   The separation between:
   - @Agent services (MCP/A2A only)
   - @UCPBusiness controllers (REST + MCP)

   This enforces UCP's requirement that standard capabilities must support REST transport.
3. **Type Safety**
   Annotations provide compile-time checking for:
   - Capability names
   - Version formats
   - Required fields (spec and schema URLs)

#### Future Considerations

These are the areas for potential improvement which I will consider in future iterations:
1. **Validation Complexity**
   Annotation values (like YYYY-MM-DD version format) must be validated at runtime. The controller includes this logic, but it adds complexity.
2. **Framework Coupling**
   The approach tightly couples business logic to:
   - UCP framework
   - Spring's annotation processing

   This could make testing and porting more challenging.
3. **Runtime Overhead**
   Annotation scanning at startup adds initialization time, though this is typically negligible for most applications.

### Implementation Quality and improvements

Current implementation handles complexity well by:

- Enforcing Single Merchant: One UCPAware implementation per host
- Namespace Validation: Validates namespace authority for spec URLs
- Automatic Profile Generation: Generates compliant discovery profiles
- Dual Capability Support: Supports both standard and custom capabilities

The annotation approach is particularly effective for UCP because it maps naturally to the protocol's capability-based design and enables the dynamic discovery that UCP emphasizes.

#### Future

- Build-Time Validation: I will Consider adding more build-time validation to catch annotation errors earlier
- Decouple Framework: Explore ways to decouple UCP logic from Spring for better testability
- Performance Optimization: Investigate caching strategies for large applications with many capabilities
- Enhanced Tooling: Develop IDE plugins to assist with annotation usage and validation
- Schema Generation: Automate generation of extension schemas based on annotated methods

## UCP Protocol Version

Implements UCP specification version: **2026-01-11**

## Documentation

- [UCP Specification](https://github.com/Universal-Commerce-Protocol/ucp/tree/main/spec)
- [API Documentation](http://localhost:8080/swagger-ui.html) (when running)
- [tools4ai Framework](https://github.com/vishalmysore/Tools4AI)
- [a2ajava Library](https://github.com/vishalmysore/a2ajava)

## Usage
Below is the example of how to use the UCP protocol.
 using curl command

  
      
      Request is like this

```bash
      {
      "jsonrpc": "2.0",
      "method": "whatThisPersonFavFood",
      "params": {
      "_meta": {
      "ucp": {
      "profile": "https://platform.example/profiles/v2026-01/shopping-agent.json"
      }
      },
      "provideAllValuesInPlainEnglish": "vishal is coming home what should i cook"
      },
      "id": 17
      }
```
```bash
      response is like this
      {
      "jsonrpc": "2.0",
      "id": 17,
      "result": {
      "ucp": {
      "version": "2026-01-11",
      "capabilities": [
      {
      "name": "io.github.vishalmysore.car_booking",
      "version": "2026-01-19"
      }
      ]
      },
      "booking_id": "bk_1234567890",
      "status": "confirmed",
      "car_model": "Toyota Camry",
      "booking_date": "2026-01-25"
      }
      }
      
   ```  