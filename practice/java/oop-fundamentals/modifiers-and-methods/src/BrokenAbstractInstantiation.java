public class BrokenAbstractInstantiation {
    abstract static class Shape {
        abstract double area();
    }

    public static void main(String[] args) {
        Shape s = new Shape(); // ERROR: Shape is abstract; cannot be instantiated
        System.out.println(s.area());
    }
}
