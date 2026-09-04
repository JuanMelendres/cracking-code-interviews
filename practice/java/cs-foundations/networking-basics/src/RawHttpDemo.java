import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Speaks raw HTTP over a raw TCP socket, with no HTTP client or server
 * library involved anywhere -- to make one fact directly observable rather
 * than asserted: HTTP is plain text, sent over an ordinary TCP byte stream.
 * A minimal server (java.net.ServerSocket) accepts one real TCP connection
 * and prints the exact bytes it received; a minimal client (java.net.Socket)
 * sends a hand-written HTTP/1.1 request and prints the exact bytes it gets back.
 */
public class RawHttpDemo {

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(0); // OS assigns a free port
        int port = serverSocket.getLocalPort();
        System.out.println("Server listening on 127.0.0.1:" + port);

        Thread server = new Thread(() -> runServer(serverSocket));
        server.start();

        runClient(port);

        server.join();
    }

    private static void runServer(ServerSocket serverSocket) {
        try (Socket connection = serverSocket.accept()) {
            System.out.println("\n--- Server: TCP connection accepted from " + connection.getRemoteSocketAddress() + " ---");

            InputStream in = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int read = in.read(buffer); // one read: the whole request arrives as an ordinary byte stream
            String rawRequest = new String(buffer, 0, read, StandardCharsets.US_ASCII);

            System.out.println("--- Server: exact raw bytes received (this is the entire HTTP request) ---");
            System.out.println(rawRequest.replace("\r\n", "\\r\\n\n"));

            String body = "{\"message\":\"hello from a raw socket\"}";
            String rawResponse =
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

            OutputStream out = connection.getOutputStream();
            out.write(rawResponse.getBytes(StandardCharsets.UTF_8));
            out.flush();

            System.out.println("--- Server: exact raw bytes sent back ---");
            System.out.println(rawResponse.replace("\r\n", "\\r\\n\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runClient(int port) throws IOException {
        try (Socket socket = new Socket("127.0.0.1", port)) {
            System.out.println("\n--- Client: TCP connection established, local port " + socket.getLocalPort()
                + " -> remote port " + socket.getPort() + " ---");

            String rawRequest =
                "GET /status HTTP/1.1\r\n" +
                "Host: 127.0.0.1:" + port + "\r\n" +
                "User-Agent: RawHttpDemo/1.0\r\n" +
                "Connection: close\r\n" +
                "\r\n";

            OutputStream out = socket.getOutputStream();
            out.write(rawRequest.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            InputStream in = socket.getInputStream();
            byte[] buffer = new byte[4096];
            int read = in.read(buffer);
            String rawResponse = new String(buffer, 0, read, StandardCharsets.UTF_8);

            System.out.println("--- Client: exact raw bytes received back from the server ---");
            System.out.println(rawResponse.replace("\r\n", "\\r\\n\n"));
        }
    }
}
