import java.util.TreeSet;

public class ComparableInconsistentWithEqualsDemo {

    // compareTo() only looks at price; equals()/hashCode() look at name+price.
    // This is "compareTo() inconsistent with equals()" -- legal Java, but
    // TreeSet/TreeMap use compareTo() EXCLUSIVELY for both ordering AND
    // duplicate detection, silently ignoring equals().
    static class Product implements Comparable<Product> {
        final String name;
        final double price;

        Product(String name, double price) { this.name = name; this.price = price; }

        @Override
        public int compareTo(Product o) { return Double.compare(this.price, o.price); }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Product p)) return false;
            return name.equals(p.name) && Double.compare(price, p.price) == 0;
        }

        @Override
        public int hashCode() { return java.util.Objects.hash(name, price); }

        @Override
        public String toString() { return name + "($" + price + ")"; }
    }

    public static void main(String[] args) {
        Product widget = new Product("Widget", 9.99);
        Product gadget = new Product("Gadget", 9.99);

        System.out.println("== Two genuinely different products, same price ==");
        System.out.println("widget.equals(gadget) = " + widget.equals(gadget) + "  (different names -- NOT equal)");
        System.out.println("widget.compareTo(gadget) = " + widget.compareTo(gadget) + "  (same price -- compareTo says EQUAL)");

        TreeSet<Product> catalog = new TreeSet<>();
        catalog.add(widget);
        boolean added = catalog.add(gadget);
        System.out.println();
        System.out.println("TreeSet<Product> catalog.add(widget); catalog.add(gadget) returned: " + added);
        System.out.println("catalog now contains: " + catalog);
        System.out.println("catalog.size() = " + catalog.size()
                + "  (gadget was SILENTLY DROPPED -- TreeSet used compareTo()==0 to decide 'duplicate', "
                + "even though equals() says they are different products)");
    }
}
