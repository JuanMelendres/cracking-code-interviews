// Java 21. Strategy: encapsulate an interchangeable algorithm behind a common
// interface, and let the caller swap implementations at runtime -- without a
// single if/else or switch on a "type" field anywhere in the client code.

interface DiscountStrategy {
    double apply(double price);
}

class NoDiscount implements DiscountStrategy {
    public double apply(double price) { return price; }
}

class PercentageDiscount implements DiscountStrategy {
    private final double percent;
    PercentageDiscount(double percent) { this.percent = percent; }
    public double apply(double price) { return price * (1 - percent); }
}

class FlatDiscount implements DiscountStrategy {
    private final double amount;
    FlatDiscount(double amount) { this.amount = amount; }
    public double apply(double price) { return Math.max(0, price - amount); }
}

class Checkout {
    private DiscountStrategy strategy;
    Checkout(DiscountStrategy strategy) { this.strategy = strategy; }
    void setStrategy(DiscountStrategy strategy) { this.strategy = strategy; }
    double total(double price) { return strategy.apply(price); } // no branching on discount TYPE anywhere
}

class StrategyDemo {
    public static void main(String[] args) {
        Checkout checkout = new Checkout(new NoDiscount());
        double price = 100.0;

        System.out.println("== Same client code (checkout.total(price)), different injected strategy ==");
        System.out.println("NoDiscount:         total(100) = " + checkout.total(price));

        checkout.setStrategy(new PercentageDiscount(0.10));
        System.out.println("PercentageDiscount(10%): total(100) = " + checkout.total(price));

        checkout.setStrategy(new FlatDiscount(15));
        System.out.println("FlatDiscount($15):  total(100) = " + checkout.total(price));

        System.out.println();
        System.out.println("Notice: Checkout.total() never inspects WHICH strategy it holds -- adding a");
        System.out.println("fourth discount type requires zero changes to Checkout, only a new class.");
    }
}
