package demo;

/** Level-0 Richardson Maturity Model shape: one URI, one verb (POST),
 * an "action" field carrying what would otherwise be the HTTP method
 * and the resource path. Used only to contrast against the real
 * resource-oriented endpoints in this pack. */
public class RpcRequest {
    public String action;
    public long id;

    public RpcRequest() { }
}
