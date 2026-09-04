# Networking Basics — Real, Executed Evidence

Evidence base for [Networking Basics: TCP/IP and HTTP Mechanics Below the Spring MVC Layer](../../../../syllabus/01-computer-science-foundations/networking-basics.md) (T-2005). One demo, no HTTP client or server library anywhere — `java.net.ServerSocket` and `java.net.Socket` only — to make directly observable, rather than asserted, that HTTP is plain text sent over an ordinary TCP byte stream.

```bash
javac -d out src/RawHttpDemo.java
java -cp out RawHttpDemo
```

## Real captured output

```
Server listening on 127.0.0.1:54233

--- Server: TCP connection accepted from /127.0.0.1:54234 ---

--- Client: TCP connection established, local port 54234 -> remote port 54233 ---
--- Server: exact raw bytes received (this is the entire HTTP request) ---
GET /status HTTP/1.1\r\n
Host: 127.0.0.1:54233\r\n
User-Agent: RawHttpDemo/1.0\r\n
Connection: close\r\n
\r\n

--- Server: exact raw bytes sent back ---
HTTP/1.1 200 OK\r\n
Content-Type: application/json\r\n
Content-Length: 37\r\n
Connection: close\r\n
\r\n
{"message":"hello from a raw socket"}
--- Client: exact raw bytes received back from the server ---
HTTP/1.1 200 OK\r\n
Content-Type: application/json\r\n
Content-Length: 37\r\n
Connection: close\r\n
\r\n
{"message":"hello from a raw socket"}
```

## What this actually shows

- **The client's local port (`54234`) and the server's listening port (`54233`) are two different, real numbers** — every TCP connection is identified by a 4-tuple (local IP, local port, remote IP, remote port), not just "the port the server is on." The client's ephemeral port was assigned by the OS at connection time; nothing in application code chose it.
- **The entire HTTP request is nothing but a sequence of ASCII text lines, each ending in `\r\n` (carriage return + line feed, not just `\n`), with a blank line separating headers from any body.** `Spring`'s `@GetMapping`, its `HttpServletRequest`, and every other layer of an HTTP framework exist to parse and construct exactly this text — none of it is a different wire format underneath.
- **The response's `Content-Length` header is the only thing telling the client how many bytes of body to read** — nothing about TCP itself marks where the response ends; TCP delivers an ordered, reliable byte stream with no built-in message boundaries at all. This is precisely why `Content-Length` (or, for a body of unknown length upfront, chunked transfer encoding) is a required part of HTTP's own framing, not TCP's.
- **`Connection: close`** is what tells the server to close the TCP connection after this one response, matching this demo's intentionally simple one-request-one-connection design — HTTP/1.1 defaults to keeping the connection open for reuse (`Connection: keep-alive`) precisely to avoid paying the cost of a fresh TCP handshake for every single request, the chapter's own Section 5 covers this trade-off directly.
