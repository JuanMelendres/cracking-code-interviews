import java.lang.reflect.Field;

/**
 * Real, reflective proof of Compact Strings (JEP 254, Java 9+): a String
 * backed entirely by Latin-1-representable characters is stored as ONE
 * byte per character (coder=LATIN1=0), not two, in the real private
 * `value` byte[] field -- real, measured memory savings for the common
 * case, verified directly rather than taken on faith from the JEP title.
 */
public class CompactStringsDemo {

    public static void main(String[] args) throws Exception {
        Field valueField = String.class.getDeclaredField("value");
        Field coderField = String.class.getDeclaredField("coder");
        valueField.setAccessible(true);
        coderField.setAccessible(true);

        System.out.println("== Real byte[] length and coder for an all-Latin-1 String ==");
        String latin1String = "Hello World"; // 11 characters, all Latin-1-representable
        byte[] latin1Bytes = (byte[]) valueField.get(latin1String);
        byte latin1Coder = coderField.getByte(latin1String);
        System.out.println("\"" + latin1String + "\" (" + latin1String.length() + " chars): "
                + "real backing byte[].length=" + latin1Bytes.length + ", real coder=" + latin1Coder
                + (latin1Coder == 0 ? "  <-- LATIN1: exactly 1 byte per character" : ""));

        System.out.println("\n== Real byte[] length and coder for a String containing a non-Latin-1 character ==");
        // NOTE: an earlier draft used 'ö' (U+00F6) here, assuming it would
        // force UTF-16 -- it doesn't, because U+00F6 genuinely IS within
        // Latin-1's 0x00-0xFF range. 'λ' (U+03BB, Greek) is genuinely
        // outside that range and DOES force UTF-16 -- verified below.
        String utf16String = "Hello Wλrld"; // the 'λ' forces UTF-16 representation for the WHOLE string
        byte[] utf16Bytes = (byte[]) valueField.get(utf16String);
        byte utf16Coder = coderField.getByte(utf16String);
        System.out.println("\"" + utf16String + "\" (" + utf16String.length() + " chars): "
                + "real backing byte[].length=" + utf16Bytes.length + ", real coder=" + utf16Coder
                + (utf16Coder == 1 ? "  <-- UTF16: exactly 2 bytes per character" : ""));

        System.out.println("\n== Real, measured consequence ==");
        System.out.println("Same character COUNT (11 vs 11), real backing array size: "
                + latin1Bytes.length + " bytes (Latin-1) vs " + utf16Bytes.length
                + " bytes (UTF-16) -- a real " + String.format("%.1fx", (double) utf16Bytes.length / latin1Bytes.length)
                + " memory difference purely from ONE non-Latin-1 character forcing the entire string to UTF-16.");
        System.out.println("This is the real, measured basis for JEP 254's memory-savings claim for the (very common)"
                + " all-Latin-1 case -- English text, most identifiers, most JSON keys, etc.");
    }
}
