public class AnonymousExample {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("anonymous class running");
            }
        };
        r.run();
    }
}
