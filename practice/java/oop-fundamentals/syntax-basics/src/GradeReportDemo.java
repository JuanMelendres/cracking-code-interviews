import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the basic building blocks of Java syntax -- variables and
 * primitive types, operators, if/else, a for loop, a while loop, a switch
 * statement, methods with parameters and return values, and arrays -- all
 * working together on one small, real task: turning a list of test scores
 * into a grade report.
 */
public class GradeReportDemo {

    // A method: a named, reusable block of code with parameters and a
    // return value. This one takes an int and returns a char -- it can be
    // called as many times as needed instead of repeating this logic.
    static char scoreToLetterGrade(int score) {
        // if/else: branches based on a boolean condition, checked in order.
        if (score >= 90) {
            return 'A';
        } else if (score >= 80) {
            return 'B';
        } else if (score >= 70) {
            return 'C';
        } else if (score >= 60) {
            return 'D';
        } else {
            return 'F';
        }
    }

    // switch: branches on a single value's exact match -- a cleaner
    // alternative to a long if/else chain when checking one variable
    // against several fixed possibilities.
    static String describeLetterGrade(char grade) {
        switch (grade) {
            case 'A':
                return "Excellent";
            case 'B':
                return "Good";
            case 'C':
                return "Satisfactory";
            case 'D':
                return "Needs improvement";
            default:
                return "Failing";
        }
    }

    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private static void check(boolean condition, String description) {
        assertions++;
        if (!condition) {
            failures.add(description);
        }
    }

    public static void main(String[] args) {
        // Variables and an array: `scores` holds a fixed-size, indexed
        // collection of primitive int values, declared with its type.
        int[] scores = {95, 82, 71, 58, 89, 63, 40};

        // A for loop: repeats a fixed number of times, indexing into the
        // array -- the most common way to process every element in order.
        int sum = 0;
        for (int i = 0; i < scores.length; i++) {
            sum += scores[i]; // += is a compound assignment operator
        }
        double average = (double) sum / scores.length; // explicit cast to avoid integer division

        check(sum == 498, "for loop correctly sums all 7 scores");
        check(Math.abs(average - 71.14) < 0.01, "average is computed as a real decimal, not truncated by integer division");

        // A while loop: repeats as long as a condition holds, used here to
        // find the FIRST failing score without knowing in advance how many
        // iterations that will take.
        int index = 0;
        int firstFailingScore = -1;
        while (index < scores.length) {
            if (scores[index] < 60) {
                firstFailingScore = scores[index];
                break; // exits the loop immediately once found
            }
            index++;
        }
        check(firstFailingScore == 58, "while loop finds the first score below 60");

        // Calling the method above once per array element, using its
        // return value directly.
        char[] letterGrades = new char[scores.length];
        for (int i = 0; i < scores.length; i++) {
            letterGrades[i] = scoreToLetterGrade(scores[i]);
        }
        check(letterGrades[0] == 'A', "95 maps to grade A");
        check(letterGrades[3] == 'F', "58 maps to grade F");
        check(letterGrades[2] == 'C', "71 maps to grade C");

        // Calling the second method, which uses switch internally.
        check(describeLetterGrade('A').equals("Excellent"), "switch correctly maps A to its description");
        check(describeLetterGrade('F').equals("Failing"), "switch's default case correctly handles F");

        // Boolean logic with && and ||: a score counts as "honor roll" only
        // if it's both an A grade AND at least 93.
        int honorRollCount = 0;
        for (int i = 0; i < scores.length; i++) {
            if (letterGrades[i] == 'A' && scores[i] >= 93) {
                honorRollCount++;
            }
        }
        check(honorRollCount == 1, "&& requires BOTH conditions true -- only the single 95 score qualifies, not every A");

        System.out.println((failures.isEmpty() ? "PASS" : "FAIL") + " " + (assertions - failures.size()) + "/" + assertions + " assertions");
        for (String f : failures) {
            System.out.println("  FAILED: " + f);
        }
        if (!failures.isEmpty()) {
            System.exit(1);
        }
    }
}
