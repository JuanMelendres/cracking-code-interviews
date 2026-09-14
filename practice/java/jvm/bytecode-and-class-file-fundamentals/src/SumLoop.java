public class SumLoop {
    private int total;

    public int sumTo(int n) {
        int result = 0;
        for (int i = 1; i <= n; i++) {
            result += i;
        }
        this.total = result;
        return result;
    }
}
