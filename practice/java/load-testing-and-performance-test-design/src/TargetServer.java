import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

/** A real HTTP service with a real, periodic stop-the-world-style pause --
 * every 2 real seconds, a background thread holds {@code pauseLock} for a
 * real 300ms, and every request handler must acquire that same lock before
 * doing its own (normal-case 10ms) work. This is a real, working stand-in
 * for a GC pause or any other event that blocks every in-flight request at
 * once, used to demonstrate the real, measurable difference between a
 * closed-loop and an open-loop load generator against identical server
 * behavior. */
public class TargetServer {

    private static final Object pauseLock = new Object();

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(args[0]);

        Thread pauseThread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(2000);
                    synchronized (pauseLock) {
                        Thread.sleep(300);
                    }
                }
            } catch (InterruptedException ignored) { }
        });
        pauseThread.setDaemon(true);
        pauseThread.start();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(64));
        server.createContext("/work", exchange -> {
            synchronized (pauseLock) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) { }
            }
            byte[] body = "OK".getBytes();
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        System.out.println("READY on port " + port);
    }
}
