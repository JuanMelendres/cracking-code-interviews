package demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * The single change that turns "the logs show nothing" into a bounded problem.
 *
 * <p>It does not need to know why a request failed. It records, unconditionally
 * and at INFO, that a request happened and what status the client received — so
 * a 500 is at minimum *visible* even when the code that produced it logged
 * nothing at all. The throwable is recorded from a catch block rather than
 * inferred from the status, because a container-generated error sets the status
 * after this frame has already unwound (see
 * syllabus/05-spring/request-filters-interceptors-and-the-servlet-chain.md).
 */
class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger("access");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
            emit(request, response.getStatus(), start, null);
        } catch (Exception ex) {
            emit(request, 500, start, ex);
            throw ex;
        }
    }

    private void emit(HttpServletRequest request, int status, long startNanos, Exception ex) {
        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        log.info("method={} uri={} status={} duration_ms={} escaped_exception={}",
                request.getMethod(), request.getRequestURI(), status, millis,
                ex == null ? "none" : ex.getClass().getSimpleName());
    }
}
