import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

// Uses java.net.http.WebSocket -- the JDK's own real, built-in WebSocket
// CLIENT (since Java 11) -- against this pack's own real, minimal WebSocket
// SERVER. Proves both directions of a real, full-duplex connection: a
// client-initiated message that gets echoed back, and a server-initiated
// push the client never asked for.
public class WebSocketClientDemo {

    public static void main(String[] args) throws Exception {
        WebSocketServer server = new WebSocketServer();
        server.start(9220);

        try {
            CountDownLatch receivedEcho = new CountDownLatch(1);
            CountDownLatch receivedPush = new CountDownLatch(1);

            WebSocket.Listener listener = new WebSocket.Listener() {
                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    String text = data.toString();
                    System.out.println("[client] received: " + text);
                    if (text.startsWith("echo:")) {
                        receivedEcho.countDown();
                    } else if (text.startsWith("unsolicited")) {
                        receivedPush.countDown();
                    }
                    webSocket.request(1);
                    return null;
                }
            };

            HttpClient httpClient = HttpClient.newHttpClient();
            WebSocket webSocket = httpClient.newWebSocketBuilder()
                    .buildAsync(URI.create("ws://localhost:9220/"), listener)
                    .join();

            System.out.println("=== Real WebSocket handshake complete -- client sending a real message ===");
            webSocket.sendText("hello from client", true);

            boolean echoReceived = receivedEcho.await(3, TimeUnit.SECONDS);
            System.out.println("Real echo received within 3s: " + echoReceived);

            System.out.println();
            System.out.println("=== Waiting for a real, UNSOLICITED server-initiated push (no client request) ===");
            boolean pushReceived = receivedPush.await(3, TimeUnit.SECONDS);
            System.out.println("Real unsolicited push received within 3s: " + pushReceived);

            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "done");
        } finally {
            server.stop();
        }
    }
}
