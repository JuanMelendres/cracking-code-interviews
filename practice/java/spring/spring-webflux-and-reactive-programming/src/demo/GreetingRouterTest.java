package demo;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

// Proves a real WebFlux RouterFunction end-to-end using WebTestClient bound
// directly to it -- no real Netty server needed to verify real routing,
// request-predicate matching, and reactive response handling.
public class GreetingRouterTest {

    @Test
    void greetEndpointReturnsGreeting() {
        WebTestClient client = WebTestClient.bindToRouterFunction(GreetingRouter.routes()).build();

        client.get().uri("/greet?name=Ada")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Ada");
    }
}
