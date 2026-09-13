public final class Product {
    final String id;
    final String name;
    final int stock;

    Product(String id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" + id + ", " + name + ", stock=" + stock + "}";
    }
}
