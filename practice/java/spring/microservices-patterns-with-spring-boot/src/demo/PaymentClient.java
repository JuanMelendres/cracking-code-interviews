package demo;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

/** Spring's own declarative HTTP client (Spring Framework 6.0+, built
 * into spring-web -- no Spring Cloud OpenFeign dependency needed). An
 * {@code @HttpExchange}-annotated interface plus {@code
 * HttpServiceProxyFactory} generates the implementation at runtime, the
 * same idea Feign popularized but now first-party. */
public interface PaymentClient {

    @PostExchange("/charge")
    ChargeResponse charge(@RequestBody ChargeRequest request);
}
