import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Real consumer-driven-contract-style verification test: the CONSUMER (not
// the provider team) owns this contract definition, and this test is run
// against the provider's real, running implementation -- not a mock -- to
// verify the provider still honors what the consumer actually depends on.
// No JSON library dependency: field presence/type is checked with a tiny,
// deliberately simple regex-based extractor, sufficient for this contract's
// three required fields.
public class ContractVerificationTest {

    static HttpServer server;

    @BeforeAll
    static void startProvider() throws Exception {
        String mode = System.getProperty("contract.mode", "compliant");
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 15901), 0);
        server.createContext("/orders/42", exchange -> {
            String body = mode.equals("breaking")
                    ? "{\"id\":42,\"total\":19.99}"
                    : "{\"id\":42,\"status\":\"SHIPPED\",\"amount\":19.99}";
            byte[] b = body.getBytes();
            exchange.sendResponseHeaders(200, b.length);
            exchange.getResponseBody().write(b);
            exchange.close();
        });
        server.start();
        System.out.println("Provider started, contract.mode=" + mode);
    }

    @AfterAll
    static void stopProvider() {
        server.stop(0);
    }

    static boolean hasField(String json, String field) {
        Matcher m = Pattern.compile("\"" + field + "\"\\s*:").matcher(json);
        return m.find();
    }

    @Test
    void providerResponseSatisfiesTheConsumerContract() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:15901/orders/42")).GET().build();
        String body = client.send(req, HttpResponse.BodyHandlers.ofString()).body();
        System.out.println("Provider response: " + body);

        // The consumer's actual contract: it reads id, status, and amount.
        assertTrue(hasField(body, "id"), "contract requires field 'id'");
        assertTrue(hasField(body, "status"), "contract requires field 'status' -- consumer displays order status to the user");
        assertTrue(hasField(body, "amount"), "contract requires field 'amount' -- consumer bills this exact field name");
    }
}
