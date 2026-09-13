import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

// Real HTTP provider, two modes selected by args[0]:
//  "compliant" -- serves the response shape the consumer contract expects.
//  "breaking"  -- simulates a real breaking change: "amount" renamed to "total",
//                 and "status" removed entirely.
public class OrderProvider {
    public static void main(String[] args) throws Exception {
        String mode = args.length > 0 ? args[0] : "compliant";
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 15900), 0);
        server.createContext("/orders/42", exchange -> {
            String body = mode.equals("breaking")
                    ? "{\"id\":42,\"total\":19.99}"
                    : "{\"id\":42,\"status\":\"SHIPPED\",\"amount\":19.99}";
            byte[] b = body.getBytes();
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, b.length);
            exchange.getResponseBody().write(b);
            exchange.close();
        });
        server.start();
        System.out.println("OrderProvider listening on :15900, mode=" + mode);
        Thread.sleep(3000);
        server.stop(0);
    }
}
