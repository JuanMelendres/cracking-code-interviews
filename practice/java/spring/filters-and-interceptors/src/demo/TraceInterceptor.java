package demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * A Spring MVC {@link HandlerInterceptor} — a Spring type, not a Servlet type.
 * It runs INSIDE the DispatcherServlet, after handler resolution, so unlike a
 * filter it is handed the resolved {@link HandlerMethod} and can see exactly
 * which controller method is about to run.
 *
 * <p>{@code blockOnPath} makes {@code preHandle} return {@code false} for one
 * URI, which short-circuits the request. The transcript shows precisely which
 * callbacks are skipped when that happens.
 */
final class TraceInterceptor implements HandlerInterceptor {

    private final String name;
    private final String blockOnPath;

    TraceInterceptor(String name, String blockOnPath) {
        this.name = name;
        this.blockOnPath = blockOnPath;
    }

    private static String describe(Object handler) {
        if (handler instanceof HandlerMethod method) {
            return "handler=" + method.getBeanType().getSimpleName() + "#" + method.getMethod().getName()
                    + " (RESOLVED -- a filter cannot see this)";
        }
        return "handler=" + handler.getClass().getSimpleName();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (request.getRequestURI().equals(blockOnPath)) {
            RequestTrace.record("Interceptor[" + name + "] preHandle",
                    describe(handler) + " -> returning FALSE (short-circuit)");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("blocked by " + name + "\n");
            return false;
        }

        RequestTrace.record("Interceptor[" + name + "] preHandle", describe(handler) + " -> returning true");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        RequestTrace.record("Interceptor[" + name + "] postHandle",
                "runs only when the handler returned normally");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        // The `ex` parameter is the interesting one. It is NOT "the exception
        // the controller threw" -- it is the exception still unresolved at this
        // point. Anything a HandlerExceptionResolver (including
        // @RestControllerAdvice) already handled arrives here as null.
        RequestTrace.record("Interceptor[" + name + "] afterCompletion",
                "ex=" + (ex == null ? "null" : ex.getClass().getSimpleName() + ": " + ex.getMessage())
                        + ", status=" + response.getStatus());
    }
}
