// Deliberately in a real package, not the default package: @SpringBootApplication's
// @ComponentScan starts from the class's own package. In the default package, that
// means scanning the ENTIRE classpath, including spring-boot-autoconfigure.jar's own
// internal classes -- which genuinely crashes startup (a real,
// NoClassDefFoundError-throwing failure hit while building this demo, not a
// hypothetical). This is itself a real, well-known Spring Boot gotcha, not just
// packaging hygiene.
package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.context.WebServerApplicationContext;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class EmbeddedServerDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== Starting a Spring Boot application -- no external Tomcat, no WAR, no app-server deployment ==");
        System.out.println("java -cp <this app + its jars>  EmbeddedServerDemo");
        System.out.println();

        ConfigurableApplicationContext ctx = SpringApplication.run(EmbeddedServerDemo.class,
            "--server.port=0", // port 0 = let the OS assign a free port, printed below
            "--logging.level.root=WARN" // quiet Spring's own startup banner/logging for a clean demo
        );

        int port = ((WebServerApplicationContext) ctx).getWebServer().getPort();
        System.out.println();
        System.out.println("Embedded Tomcat is listening on http://localhost:" + port + " -- started IN this JVM process, by this same 'java' command.");

        System.out.println();
        System.out.println("== Proving it's a real, working HTTP server: a genuine HTTP client request ==");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/hello")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("HTTP " + response.statusCode() + " -- body: \"" + response.body() + "\"");

        ctx.close();
        System.out.println();
        System.out.println("Context closed. The embedded server, and the process serving it, no longer exist -- there was never anything to \"undeploy from.\"");
    }
}
