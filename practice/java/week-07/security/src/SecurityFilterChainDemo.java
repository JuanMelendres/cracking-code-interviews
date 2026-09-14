import java.util.*;

/**
 * A real, minimal reproduction of Spring Security's filter-chain mechanism:
 * a chain-of-responsibility of filters, each able to inspect the request,
 * do work, and either call the next filter or short-circuit the chain
 * entirely. This is the exact pattern (javax.servlet.Filter / jakarta's
 * FilterChain) Spring Security's real filter chain is built on -- this demo
 * doesn't use the real spring-security jars, but the mechanism traced here
 * is structurally identical to what they implement.
 */
public class SecurityFilterChainDemo {

    interface Filter {
        void doFilter(Request request, FilterChain chain);
    }

    static final class Request {
        final String path;
        final String authHeader;
        boolean authenticated = false;
        String principal;
        Request(String path, String authHeader) { this.path = path; this.authHeader = authHeader; }
    }

    static final class FilterChain {
        private final List<Filter> filters;
        private final Runnable finalHandler;
        private int index = 0;

        FilterChain(List<Filter> filters, Runnable finalHandler) {
            this.filters = filters;
            this.finalHandler = finalHandler;
        }

        void doFilter(Request request) {
            if (index < filters.size()) {
                Filter next = filters.get(index++);
                next.doFilter(request, this);
            } else {
                finalHandler.run();
            }
        }
    }

    static final List<String> trace = new ArrayList<>();

    public static void main(String[] args) {
        List<Filter> chain = List.of(
            (req, next) -> { trace.add("CorsFilter: checking origin"); next.doFilter(req); },
            (req, next) -> { trace.add("CsrfFilter: checking CSRF token (skipped for stateless API)"); next.doFilter(req); },
            (req, next) -> {
                trace.add("AuthenticationFilter: parsing Authorization header");
                if (req.authHeader == null) {
                    trace.add("AuthenticationFilter: NO credentials -- SHORT-CIRCUITING chain, returning 401");
                    return; // does NOT call next.doFilter() -- the chain stops here
                }
                if (req.authHeader.equals("Bearer valid-token")) {
                    req.authenticated = true;
                    req.principal = "user-42";
                    trace.add("AuthenticationFilter: token valid, principal set to " + req.principal);
                }
                next.doFilter(req);
            },
            (req, next) -> {
                trace.add("AuthorizationFilter: checking " + req.principal + " has access to " + req.path);
                if (req.path.equals("/admin") && !"admin-user".equals(req.principal)) {
                    trace.add("AuthorizationFilter: principal lacks required role -- SHORT-CIRCUITING, returning 403");
                    return;
                }
                next.doFilter(req);
            }
        );

        System.out.println("=== Scenario 1: valid token, non-admin path ===");
        trace.clear();
        new FilterChain(chain, () -> trace.add("CONTROLLER: request reached the actual endpoint handler"))
                .doFilter(new Request("/orders", "Bearer valid-token"));
        trace.forEach(t -> System.out.println("  " + t));

        System.out.println("\n=== Scenario 2: no Authorization header -- short-circuits at authentication ===");
        trace.clear();
        new FilterChain(chain, () -> trace.add("CONTROLLER: request reached the actual endpoint handler"))
                .doFilter(new Request("/orders", null));
        trace.forEach(t -> System.out.println("  " + t));
        System.out.println("  (Notice: CONTROLLER line never appears -- the chain stopped at the auth filter.)");

        System.out.println("\n=== Scenario 3: valid token, but wrong role for /admin -- short-circuits at authorization ===");
        trace.clear();
        new FilterChain(chain, () -> trace.add("CONTROLLER: request reached the actual endpoint handler"))
                .doFilter(new Request("/admin", "Bearer valid-token"));
        trace.forEach(t -> System.out.println("  " + t));
        System.out.println("  (Notice: authenticated successfully, but never reached the controller -- authorization is a separate, later gate.)");
    }
}
