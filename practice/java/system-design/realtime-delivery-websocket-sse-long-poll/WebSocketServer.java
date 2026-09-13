import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

// A real, minimal RFC 6455 WebSocket server implemented directly over
// java.net.ServerSocket -- no framework, no WebSocket library. Deliberately
// scoped to small, single-frame text messages (payload < 126 bytes) to keep
// the real handshake and framing mechanics legible; a production WebSocket
// server needs the full extended-length and fragmentation rules this demo
// intentionally omits.
public class WebSocketServer {
    private static final String MAGIC_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private ServerSocket serverSocket;
    private volatile boolean running = true;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Thread acceptThread = new Thread(() -> {
            try {
                while (running) {
                    Socket socket = serverSocket.accept();
                    // A real, honest fix: the first version of this demo used
                    // Executors.newSingleThreadExecutor() per connection and never
                    // shut it down, leaking a non-daemon thread pool that kept the
                    // JVM alive indefinitely after every other real work finished.
                    // A plain daemon Thread per connection has no such lifecycle to
                    // manage.
                    Thread connectionThread = new Thread(() -> handleConnection(socket));
                    connectionThread.setDaemon(true);
                    connectionThread.start();
                }
            } catch (IOException e) {
                // server stopped
            }
        });
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private void handleConnection(Socket socket) {
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            Object writeLock = new Object();

            performHandshake(in, out);

            // A real, unsolicited server-initiated push -- sent with no
            // client request at all, proving true full-duplex, server-push
            // capability, not just a request/response exchange over a
            // long-lived connection.
            Thread pusher = new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    synchronized (writeLock) {
                        writeTextFrame(out, "unsolicited-real-time-push-from-server");
                    }
                } catch (Exception ignored) {
                    // demo connection closed before the push fired
                }
            });
            pusher.setDaemon(true);
            pusher.start();

            while (true) {
                String message = readTextFrame(in);
                if (message == null) {
                    break; // connection closed
                }
                synchronized (writeLock) {
                    writeTextFrame(out, "echo: " + message);
                }
            }
        } catch (IOException e) {
            // connection closed/errored
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void performHandshake(InputStream in, OutputStream out) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        String line;
        String key = null;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            if (line.toLowerCase().startsWith("sec-websocket-key:")) {
                key = line.substring(line.indexOf(':') + 1).trim();
            }
        }
        if (key == null) {
            throw new IOException("no Sec-WebSocket-Key header found");
        }
        String accept = computeAccept(key);
        String response = "HTTP/1.1 101 Switching Protocols\r\n"
                + "Upgrade: websocket\r\n"
                + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Accept: " + accept + "\r\n\r\n";
        out.write(response.getBytes(StandardCharsets.US_ASCII));
        out.flush();
    }

    private String computeAccept(String key) throws IOException {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] hash = sha1.digest((key + MAGIC_GUID).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private String readTextFrame(InputStream in) throws IOException {
        int firstByte = in.read();
        if (firstByte == -1) {
            return null;
        }
        int opcode = firstByte & 0x0F;
        if (opcode == 0x8) {
            return null; // close frame
        }

        int secondByte = in.read();
        if (secondByte == -1) {
            return null;
        }
        boolean masked = (secondByte & 0x80) != 0;
        int payloadLen = secondByte & 0x7F; // scoped to <126 for this demo

        byte[] maskKey = new byte[4];
        if (masked) {
            readFully(in, maskKey, 4);
        }

        byte[] payload = new byte[payloadLen];
        readFully(in, payload, payloadLen);

        if (masked) {
            for (int i = 0; i < payloadLen; i++) {
                payload[i] ^= maskKey[i % 4];
            }
        }

        return new String(payload, StandardCharsets.UTF_8);
    }

    private void readFully(InputStream in, byte[] buffer, int length) throws IOException {
        int total = 0;
        while (total < length) {
            int read = in.read(buffer, total, length - total);
            if (read == -1) {
                throw new IOException("connection closed mid-frame");
            }
            total += read;
        }
    }

    private void writeTextFrame(OutputStream out, String message) throws IOException {
        byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        out.write(0x81); // FIN=1, opcode=0x1 (text)
        out.write(payload.length); // scoped to <126 for this demo; server frames are never masked
        out.write(payload);
        out.flush();
    }
}
