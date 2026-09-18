package demo;

/** One HATEOAS link: the actual affordance a client can follow -- an
 * href plus the HTTP method to use against it. Real interview point:
 * HATEOAS is exactly this -- state-dependent links in the response
 * body, not a client-side hardcoded rulebook of "what can I do next." */
public class LinkInfo {
    public final String href;
    public final String method;

    public LinkInfo(String href, String method) {
        this.href = href;
        this.method = method;
    }
}
