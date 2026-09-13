/**
 * A trivial method, disassembled with `javap -c` to show what javac actually
 * produces: JVM bytecode -- a stack-machine instruction set -- not machine code.
 * The README shows the real javap output and walks through the fetch-decode-execute
 * cycle each instruction goes through, first in the bytecode interpreter and,
 * after JIT compilation, as real x86-64/ARM64 machine instructions on the CPU.
 */
public class BytecodeDisassemblyDemo {

    static int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println(add(2, 3));
    }
}
