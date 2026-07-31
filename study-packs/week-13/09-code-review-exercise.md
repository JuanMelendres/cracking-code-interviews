---
title: "Code Review Exercise — Spot the Five Java Core Antipatterns"
week: 13
document_type: study-pack-exercise
status: draft
last_reviewed: 2026-07-30
---

# Code Review Exercise — Spot the Five Java Core Antipatterns

This week's deliverable, in place of a system-design exercise: a single, plausible-looking class with one antipattern from each of this week's five topics baked in. Review it as if it were a real pull request, list every defect you find with the specific fix, before checking the worked solution.

## The code under review

```java
public class ShoppingCart {

    private final List<Item> items;
    private Date lastModified;

    public ShoppingCart(List<Item> items, Date lastModified) {
        this.items = items;                 // (A)
        this.lastModified = lastModified;    // (B)
    }

    public List<Item> getItems() {
        return items;                        // (C)
    }

    public Map<String, Double> pricesBySku() {
        return items.stream()
                .collect(Collectors.toMap(Item::sku, Item::price)); // (D)
    }

    public void reprice(PricingService pricingService) {
        try {
            pricingService.applyDiscounts(this);
        } catch (PricingException e) {
            throw new CartException("could not reprice cart"); // (E)
        }
    }

    static class Item implements Comparable<Item> {
        final String sku;
        final double price;

        Item(String sku, double price) { this.sku = sku; this.price = price; }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Item i)) return false;
            return sku.equals(i.sku) && Double.compare(price, i.price) == 0;
        }

        // (F) -- no hashCode() override

        @Override
        public int compareTo(Item o) { return Double.compare(price, o.price); } // (G)
    }
}
```

## Your task

For each of (A) through (G), identify:

1. Which of this week's five topics it touches.
2. What actually breaks (with a concrete, plausible failure scenario — not just "this is bad practice").
3. The specific fix.

Do this in writing before reading further.

---

## Worked Solution

**(A) — Constructor stores the caller's `List` reference directly (T-103, Immutability).**
Breaks: a caller who keeps a reference to the `items` list passed into the constructor can mutate the cart's contents after construction, with no method call on `ShoppingCart` itself involved.
Fix: `this.items = new ArrayList<>(items);` (or `List.copyOf(items)` if the cart's own methods never need to mutate the list internally).

**(B) — Constructor stores the caller's `Date` reference directly (T-103, Immutability).**
Breaks: identical mechanism to (A) — a caller mutating the original `Date` object via `setTime()` changes `lastModified` on the cart, even though nothing on `ShoppingCart` was called.
Fix: `this.lastModified = new Date(lastModified.getTime());`.

**(C) — Getter returns the live internal `List` reference (T-103, Immutability).**
Breaks: `cart.getItems().add(new Item(...))` silently mutates the cart's own internal state from outside the class, bypassing any invariant `ShoppingCart` might want to enforce on its item list.
Fix: `return List.copyOf(items);`.

**(D) — `Collectors.toMap()` with no merge function on data that can contain duplicate keys (T-107, Streams).**
Breaks: if two `Item`s share the same `sku` (e.g., the same product added twice, or a data error upstream), this throws `IllegalStateException` at runtime, in production, on real cart data — not a hypothetical.
Fix: `Collectors.toMap(Item::sku, Item::price, (a, b) -> b)` (or whatever merge semantics are actually correct — e.g., summing, or preferring the first).

**(E) — Exception wrapped without chaining the cause (T-105, Exception Design).**
Breaks: `CartException`'s `getCause()` is `null`; whatever `PricingException` actually said (which discount rule failed, which product was the problem) is gone the moment it's caught, making any resulting alert or log line useless for real debugging.
Fix: `throw new CartException("could not reprice cart", e);` (and ensure `CartException` has a cause-accepting constructor in the first place).

**(F) — `equals()` overridden without `hashCode()` (T-101, Equality Contracts).**
Breaks: two `Item`s with the same `sku` and `price` are `equals()`-equal but (using `Object`'s default identity hash) will very likely have different hash codes — so a `HashSet<Item>` or `HashMap<Item, ...>` used anywhere else in the codebase will silently fail to recognize them as duplicates.
Fix: `@Override public int hashCode() { return Objects.hash(sku, price); }`.

**(G) — `compareTo()` uses only `price`, inconsistent with `equals()`'s full field set (T-101, Comparable Contracts).**
Breaks: two different products (different `sku`) that happen to share a price are `equals()`-different but `compareTo()`-equal — a `TreeSet<Item>` or `TreeMap<Item, ...>` will silently drop the second one as a "duplicate," even though it's a genuinely distinct product.
Fix: either derive `compareTo()` from the same fields as `equals()` (e.g., compare by price, then break ties by sku), or don't implement `Comparable` on `Item` at all and use an explicit `Comparator` for any price-based sorting need instead.

## Self-Check

- [ ] Found all seven defects before reading the solution
- [ ] For each, named the specific failure scenario, not just "this is a known bad practice"
- [ ] For each, wrote the specific one-line (or near one-line) fix
- [ ] Can explain, for (F) and (G) specifically, why the bug produces no exception and no visible error — only a silently wrong collection
