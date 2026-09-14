package demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class GreetingController {
    @GetMapping("/hello")
    String hello() {
        return "Hello from an embedded server -- no external Tomcat was ever deployed to.";
    }
}
