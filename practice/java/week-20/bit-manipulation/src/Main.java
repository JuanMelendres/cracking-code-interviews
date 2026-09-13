import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // LC 136
        Check.eq(1, Problems.singleNumber(new int[]{2, 2, 1}), "LC136 singleNumber([2,2,1]) = 1");
        Check.eq(4, Problems.singleNumber(new int[]{4, 1, 2, 1, 2}), "LC136 singleNumber([4,1,2,1,2]) = 4");

        // LC 191
        Check.eq(3, Problems.hammingWeight(0b00000000000000000000000000001011), "LC191 hammingWeight(0b1011) = 3");
        Check.eq(31, Problems.hammingWeight(0b11111111111111111111111111111101), "LC191 hammingWeight(almost all 1s) = 31");

        // LC 268
        Check.eq(2, Problems.missingNumber(new int[]{3, 0, 1}), "LC268 missingNumber([3,0,1]) = 2");
        Check.eq(8, Problems.missingNumber(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}), "LC268 missingNumber(9 elements) = 8");

        // LC 371
        Check.eq(3, Problems.getSum(1, 2), "LC371 getSum(1, 2) = 3, no + operator used");
        Check.eq(0, Problems.getSum(-5, 5), "LC371 getSum(-5, 5) = 0, negative operands via two's complement");

        // LC 338
        Check.eq("[0, 1, 1]", Arrays.toString(Problems.countBits(2)), "LC338 countBits(2) = [0,1,1]");
        Check.eq("[0, 1, 1, 2, 1, 2]", Arrays.toString(Problems.countBits(5)), "LC338 countBits(5) = [0,1,1,2,1,2]");

        Check.summary("Week 20 — Bit Manipulation (LC 136, 191, 268, 371, 338)");
    }
}
