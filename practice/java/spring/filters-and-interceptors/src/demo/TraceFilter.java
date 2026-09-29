package demo;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A plain Servlet {@link Filter} — a Jakarta Servlet API type, not a Spring
 * type. It wraps the DispatcherServlet from the outside, so it runs before
 * Spring has resolved (or even looked for) a handler, and it runs again on the
 * way out after the response has been written.
 *
 * <p>{@code failOnPath} makes the filter throw for one specific URI. That is
 * the interesting case: an exception thrown here never reaches Spring MVC's
 * exception resolvers, because the DispatcherServlet it would have to pass
 * through is further down the chain and was never entered.
 */
final class TraceFilter implements Filter {

    private final String name;
    private final String failOnPath;

    TraceFilter(String name, String failOnPath) {
        this.name = name;
        this.failOnPath = failOnPath;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest http = (HttpServletRequest) request;
        String uri = http.getRequestURI();

        if (name.equals("outer-filter")) {
            RequestTrace.start();
        }

        // A filter sees the raw request. It has no idea which controller method
        // (if any) will eventually handle this URI -- handler resolution has not
        // happened yet.
        RequestTrace.record("Filter[" + name + "] BEFORE chain",
                "uri=" + uri + ", handler-known=NO (DispatcherServlet not entered yet)");

        if (uri.equals(failOnPath)) {
            RequestTrace.record("Filter[" + name + "] THROWS",
                    "about to throw ServletException -- note what does NOT run after this");
            throw new ServletException("deliberate failure inside " + name);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            // Runs even when something downstream threw: this is the filter's
            // own finally block, not any Spring callback.
            RequestTrace.record("Filter[" + name + "] AFTER chain",
                    "status=" + ((jakarta.servlet.http.HttpServletResponse) response).getStatus()
                            + ", response committed=" + response.isCommitted());
            if (name.equals("outer-filter")) {
                RequestTrace.printAndClear("TRACE for " + uri);
            }
        }
    }
}
