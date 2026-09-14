package demo;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

public class GreetingRouter {

    public static RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route(GET("/greet"), request -> {
            String name = request.queryParam("name").orElse("world");
            return ServerResponse.ok().bodyValue("Hello, " + name);
        });
    }
}
