public class BrokenDefaultMethodDiamondCollision {

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

    // ERROR: Duck inherits unrelated defaults for move() from both Flyer and Swimmer.
    // Java refuses to silently pick one — this must be an explicit compile error.
    static class Duck implements Flyer, Swimmer {
    }

    public static void main(String[] args) {
        Duck duck = new Duck();
        System.out.println(duck.move());
    }
}
