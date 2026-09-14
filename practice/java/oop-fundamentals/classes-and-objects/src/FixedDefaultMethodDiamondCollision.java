public class FixedDefaultMethodDiamondCollision {

    interface Flyer {
        default String move() {
            return "flying";
        }
    }

    interface Swimmer {
        default String move() {
            return "swimming";
        }
    }

    // FIXED: Duck explicitly overrides move(), resolving the collision itself instead
    // of asking the compiler to guess which interface's default should win.
    static class Duck implements Flyer, Swimmer {
        @Override
        public String move() {
            // Can still explicitly call either parent's version via InterfaceName.super.method().
            return Flyer.super.move() + " and " + Swimmer.super.move();
        }
    }

    static int assertionCount = 0;
    static int passCount = 0;

    static void check(boolean condition, String label) {
        assertionCount++;
        if (condition) {
            passCount++;
            System.out.println("PASS: " + label);
        } else {
            System.out.println("FAIL: " + label);
        }
    }

    public static void main(String[] args) {
        Duck duck = new Duck();
        String result = duck.move();
        System.out.println("duck.move() = \"" + result + "\"");
        check(result.equals("flying and swimming"),
                "explicit override resolves the collision, and InterfaceName.super.method() reaches each parent's own default explicitly");

        System.out.println();
        System.out.println(passCount + " / " + assertionCount + " assertions passed");
    }
}
