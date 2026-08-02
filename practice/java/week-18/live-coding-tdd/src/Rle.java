// Step 4: full run-length-encoding implementation, driven out by all three tests.
public class Rle {
    public static String encode(String input) {
        if (input.isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        char current = input.charAt(0);
        int count = 1;
        for (int i = 1; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == current) {
                count++;
            } else {
                out.append(current).append(count);
                current = c;
                count = 1;
            }
        }
        out.append(current).append(count);
        return out.toString();
    }
}
