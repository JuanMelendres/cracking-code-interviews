import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class CombinatorsDemo {
    public static int add(int a, int b) { return a + b; }
    public static String describe(int sum) { return "sum=" + sum; }

    public static void main(String[] args) throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        MethodHandle addMH = lookup.findStatic(CombinatorsDemo.class, "add",
                MethodType.methodType(int.class, int.class, int.class));
        MethodHandle describeMH = lookup.findStatic(CombinatorsDemo.class, "describe",
                MethodType.methodType(String.class, int.class));

        // filterReturnValue: pipe add(a,b)'s int result through describe(int)->String
        MethodHandle addThenDescribe = MethodHandles.filterReturnValue(addMH, describeMH);
        String result1 = (String) addThenDescribe.invoke(3, 4);
        System.out.println("filterReturnValue(add, describe).invoke(3,4) = " + result1);
        System.out.println("combined handle type: " + addThenDescribe.type());

        // dropArguments: adapt add(int,int) to accept and ignore a leading String
        MethodHandle addIgnoringPrefix = MethodHandles.dropArguments(addMH, 0, String.class);
        int result2 = (int) addIgnoringPrefix.invoke("ignored-context", 10, 20);
        System.out.println("dropArguments(add, 0, String.class).invoke(\"ignored-context\", 10, 20) = " + result2);
        System.out.println("adapted handle type: " + addIgnoringPrefix.type());
    }
}
