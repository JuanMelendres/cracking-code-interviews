# Spring Data JPA: repository abstraction (T-510) — runnable verification

Real, executed Java 21 output backing
[`syllabus/05-spring/spring-data-jpa-repository-abstraction.md`](../../../syllabus/05-spring/spring-data-jpa-repository-abstraction.md)
(T-510). Real Spring Framework 6.2.19, real Spring Data JPA 3.5.13, real
Hibernate ORM 6.6.55.Final as the JPA provider, and a real in-memory H2
database — no mocked `EntityManager`, no fabricated SQL.

## Setup

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" demo.GoodRepositoryDemo
java -cp "out:lib/*" demo.broken.BrokenRepositoryDemo
```

## `GoodRepositoryDemo` — the four query-derivation mechanisms, one repository

`OrderRepository` extends `JpaRepository<Order, Long>` and
`JpaSpecificationExecutor<Order>` over two entities (`Order` has a
one-to-many `items` association to `OrderItem`). No implementation class is
ever written by hand — Spring Data generates a JDK dynamic proxy for the
interface at context startup.

### 1. Derived query method

```java
List<Order> findByStatusAndTotalAmountGreaterThan(OrderStatus status, BigDecimal amount, Sort sort);
```

Real generated SQL, captured from the Hibernate SQL log:

```
select o1_0.id,o1_0.customerId,o1_0.status,o1_0.total_amount from orders o1_0
where o1_0.status=? and o1_0.total_amount>? order by o1_0.total_amount desc
  -> Order{id=3, customerId=cust-1, status=SHIPPED, totalAmount=300.00}
  -> Order{id=1, customerId=cust-1, status=SHIPPED, totalAmount=120.00}
```

Spring Data parsed `StatusAndTotalAmountGreaterThan` into a property tree
(`status`, `totalAmount`) checked against `Order`'s real JPA metamodel, then
built this query — no JPQL or SQL was written for this method.

### 2. Derived query method + `Pageable`

```
select ... from orders o1_0 where o1_0.status=? fetch first ? rows only
select count(o1_0.id) from orders o1_0 where o1_0.status=?
  page size=1: content.size()=1 totalElements=2 totalPages=2
```

Two real, separate SQL statements for one repository call: the page of
content, and an independent `COUNT` query for `Page#getTotalElements()`.
This is the real cost `Pageable` adds over a plain `List<T>` return —
worth knowing before reaching for it on a hot path.

### 3. `@Query` with JPQL — an entity join

```java
@Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.sku = :sku")
List<Order> findOrdersContainingSku(@Param("sku") String sku);
```

```
select distinct o1_0.id,o1_0.customerId,o1_0.status,o1_0.total_amount
from orders o1_0 join order_items i1_0 on o1_0.id=i1_0.order_id where i1_0.sku=?
  -> Order{id=1, customerId=cust-1, status=SHIPPED, totalAmount=120.00}
  -> Order{id=3, customerId=cust-1, status=SHIPPED, totalAmount=300.00}
```

JPQL operates against the **entity model** (`Order`, its `items`
association) — `DISTINCT` is required here because the join fans out one
row per matching `OrderItem`; order `id=3` has two items, but only one
(`SKU-KEYBOARD`) matches this query's `WHERE`, so its row still appears
exactly once above. Without `DISTINCT`, an order matching on more than one
item would print twice — a real duplicate-row trap this query's own shape
sets up.

### 4. `@Query` with a native query — raw SQL + interface projection

```java
@Query(value = "SELECT status AS status, COUNT(*) AS order_count, SUM(total_amount) AS total " +
        "FROM orders GROUP BY status ORDER BY status", nativeQuery = true)
List<StatusSummary> statusSummaryNative();
```

```
SELECT status AS status, COUNT(*) AS order_count, SUM(total_amount) AS total FROM orders GROUP BY status ORDER BY status
  -> status=CANCELLED orderCount=1 total=15.00
  -> status=PENDING orderCount=1 total=45.50
  -> status=SHIPPED orderCount=2 total=420.00
```

The native query bypasses JPQL/HQL translation entirely — this exact SQL
string reaches the database. `StatusSummary` is a plain interface
(`getStatus()`, `getOrderCount()`, `getTotal()`); Spring Data binds each
accessor to the matching column alias (case-insensitive,
underscore-to-camelCase) with zero manual `ResultSet` mapping or DTO
constructor.

### 5. `Specification` — dynamic predicate composition

```
select count(o1_0.id) from orders o1_0
  no filters                          -> 4 orders
select count(o1_0.id) from orders o1_0 where o1_0.status=?
  status=SHIPPED                      -> 2 orders
select count(o1_0.id) from orders o1_0 where o1_0.status=? and o1_0.total_amount>=?
  status=SHIPPED AND total>=200.00    -> 1 orders
```

`OrderSpecifications.hasStatus(...)` and `.minTotal(...)` are ordinary
static methods returning `Specification<Order>` lambdas. They compose with
`.and(...)` at **runtime**, based on which filters the caller actually
supplied — the derived-method and static-`@Query` mechanisms above are both
fixed at compile time by their own name/text and cannot express "0, 1, or 2
of N optional filters" without an explosion of overloads.

## `BrokenRepositoryDemo` — the fail-fast argument for repository interfaces

`BrokenOrderRepository` declares
`findByTotlAmountGreaterThan(BigDecimal amount)` — a real typo
(`Totl`, not `Total`). `javac` compiles it without complaint; Java has no
way to know `Order` lacks a `totlAmount` property from a string in a
method name. Real output from booting a Spring context that scans this
interface:

```
Booting a Spring context with a repository method referencing a
property that does not exist on Order (findByTotlAmountGreaterThan)...

[main] WARN ... BeanCreationException: Error creating bean with name 'brokenOrderRepository' ...
Could not create query for public abstract java.util.List
demo.broken.BrokenOrderRepository.findByTotlAmountGreaterThan(java.math.BigDecimal);
Reason: ... No property 'totlAmount' found for type 'Order'; Did you mean 'totalAmount'

=== Context refresh failed, as expected ===
Root cause class:   org.springframework.data.mapping.PropertyReferenceException
Root cause message: No property 'totlAmount' found for type 'Order'; Did you mean 'totalAmount'
```

The real, decisive fact: this fails at `ApplicationContext` **startup**
(`@EnableJpaRepositories` eagerly parses and validates every derived query
method's property path against the real JPA metamodel while creating the
repository proxy), not lazily on the method's first invocation. A typo like
this one is caught before the application ever accepts a request — a
concrete, load-bearing reason interview answers usually only gesture at
("Spring Data validates method names") without saying *when* or *why* it
matters. Spring's own message even suggests the fix ("Did you mean
'totalAmount'"), a real detail worth mentioning in an interview answer, not
an invented flourish.

## Real discoveries made while building this pack

`spring-orm`'s `LocalContainerEntityManagerFactoryBean` throws a real
`NoClassDefFoundError` for `org.springframework.jdbc.datasource.lookup.DataSourceLookup`
without `spring-jdbc` on the classpath — that class lives in `spring-jdbc`,
not `spring-orm` itself, despite `spring-orm` referencing it directly.
Documented in `fetch-deps.sh` rather than left as a silent extra jar.
