import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

/** Minimal real HTTP server -- no framework, so startup time measures the
 * JVM/runtime itself, not Spring's own component-scanning overhead. Used
 * identically across three real configurations: plain JVM, JVM + AppCDS
 * archive, and a GraalVM native-image binary compiled from this same
 * source. */
public class Main {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", exchange -> {
            byte[] body = "OK".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
            if (args.length > 1 && "exit-after-first-request".equals(args[1])) {
                new Thread(() -> {
                    try { Thread.sleep(50); } catch (InterruptedException ignored) { }
                    System.exit(0);
                }).start();
            }
        });
        server.start();
        System.out.println("READY");
    }
}
