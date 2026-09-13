import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

// A tiny, real HTTP server using only the JDK's own built-in HttpServer --
// no external dependencies, so the Docker image built from this needs
// nothing beyond a JDK base image and this one file. Its only job is to
// prove real containerization mechanics: build an image, run a container
// from it, map a port, and observe real output through that port.
public class Server {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", exchange -> {
            String response = "Hello from inside a container\n";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}
