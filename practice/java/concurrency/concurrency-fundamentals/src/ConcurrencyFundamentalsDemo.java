/**
 * Java concurrency fundamentals, measured. Java 21, no dependencies.
 *
 * Run: javac -d out src/*.java && java -cp out ConcurrencyFundamentalsDemo
 */
public final class ConcurrencyFundamentalsDemo {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("OpenJDK " + System.getProperty("java.version")
                + ", " + Runtime.getRuntime().availableProcessors() + " available processors\n");
        ThreadBasicsDemo.run();
        RaceConditionDemo.run();
        VisibilityDemo.run();
        CostDemo.run();
    }
}
