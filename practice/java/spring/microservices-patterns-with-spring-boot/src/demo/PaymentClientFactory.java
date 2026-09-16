package demo;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/** Builds the real generated {@link PaymentClient} implementation --
 * this is the "how" behind the {@code @HttpExchange} interface: a
 * RestClient does the actual HTTP work, an adapter bridges it to the
 * HTTP Interface abstraction, and HttpServiceProxyFactory generates a
 * dynamic proxy implementing the interface. */
public final class PaymentClientFactory {

    private PaymentClientFactory() {
    }

    public static PaymentClient create(String baseUrl, int readTimeoutMillis) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(readTimeoutMillis);
        requestFactory.setConnectTimeout(readTimeoutMillis);

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(PaymentClient.class);
    }
}
