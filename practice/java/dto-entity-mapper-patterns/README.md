# DTO, Entity, and Mapper Patterns — Real, Executed Demo

Backs [DTO, Entity, and Mapper Patterns](../../../syllabus/05-spring/dto-entity-mapper-patterns.md)
(T-520). Real MapStruct 1.6.3 annotation processing (its actual, real generated
Java source captured below, not a description of it), plus Spring's real
`@Component` annotation, no Maven/Gradle, jars fetched directly from Maven
Central.

## What this proves

A real, small domain: `OrderEntity` (the persistence shape, including a
genuinely sensitive `internalFraudScoreNotes` field), `OrderResponse` (the
API-facing DTO, a record), `CreateOrderRequest` (a second, differently-shaped
DTO for input), and `OrderMapper` — a real MapStruct `@Mapper` interface, not
a hand-written mapping class.

## Setup

```bash
./fetch-deps.sh
mkdir -p out generated-sources
javac -parameters -cp "lib/*" -processorpath "lib/mapstruct-processor.jar:lib/mapstruct.jar" -s generated-sources -d out src/demo/*.java
```

The `-processorpath`/`-s generated-sources` flags are what make MapStruct's
annotation processor actually run during this `javac` invocation and write
its generated source into `generated-sources/` where you can read it
yourself.

## Run

```bash
java -cp "out:lib/*" org.junit.platform.console.ConsoleLauncher --select-class demo.OrderMapperTest
```

Real output ([full capture](test-run-output.txt)):

```
Real OrderResponse record components: [id, customerName, totalAmount, createdAt]
Real mapped response with null customer: OrderResponse[id=2, customerName=null, totalAmount=50.00, createdAt=...]
Real mapped response: OrderResponse[id=1, customerName=Ada Lovelace, totalAmount=199.99, createdAt=2026-09-16T10:00:00Z]
```

## The real generated mapper (this is the actual "magic," not a black box)

`OrderMapper` is only an interface with one `@Mapping`-annotated method --
nowhere in this pack's own source does a class implement it. MapStruct's
annotation processor generates `OrderMapperImpl` at compile time. Here it is,
captured verbatim from a real build ([full capture](OrderMapperImpl-generated.txt)):

```java
@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    ...
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toResponse(OrderEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String customerName = null;
        Long id = null;
        BigDecimal totalAmount = null;
        Instant createdAt = null;

        customerName = entityCustomerName( entity );
        id = entity.getId();
        totalAmount = entity.getTotalAmount();
        createdAt = entity.getCreatedAt();

        OrderResponse orderResponse = new OrderResponse( id, customerName, totalAmount, createdAt );

        return orderResponse;
    }

    private String entityCustomerName(OrderEntity orderEntity) {
        Customer customer = orderEntity.getCustomer();
        if ( customer == null ) {
            return null;
        }
        return customer.getName();
    }
}
```

Three real, verified things this generated code proves:

1. **`@Component` is really there** — `componentModel = "spring"` on the
   `@Mapper` annotation makes MapStruct generate a real Spring bean, wired
   into a service exactly like any hand-written `@Component`, with zero
   manual `@Bean` method needed.
2. **The null-safety is real, generated defensive code** — `entity ==
   null` and the private `entityCustomerName` helper's own `customer ==
   null` check were never written by hand; MapStruct generates them because
   `customerName` is sourced from a nested, nullable property
   (`customer.name`).
3. **`internalFraudScoreNotes` appears nowhere in this file** — not
   commented out, not skipped with a warning suppressed, genuinely never
   referenced. `OrderMapperTest.orderResponse_hasNoFieldForTheSensitiveInternalNote_compileTimeProof`
   proves this isn't an accident of this one run: `OrderResponse`, as a
   Java record, structurally has no component for that field at all — the
   leak isn't just "didn't happen this time," it's compile-time impossible.

## Real discoveries made while building this pack

MapStruct compiled clean on the first real run with just `mapstruct.jar` +
`mapstruct-processor.jar` on the processor path — no additional JAXB/XML
jars needed despite `mapstruct-processor`'s own POM listing
`jakarta.xml.bind-api`/`jaxb-api` as `provided`-scope dependencies. Those
appear to back an optional code path (likely JAXB-annotated bean mapping)
this pack's simple record/POJO mapping never exercises.
