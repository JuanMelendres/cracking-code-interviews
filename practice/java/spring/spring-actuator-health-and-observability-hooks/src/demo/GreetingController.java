package demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private final GreetingCounterService greetingCounterService;

    public GreetingController(GreetingCounterService greetingCounterService) {
        this.greetingCounterService = greetingCounterService;
    }

    @GetMapping("/greet")
    public String greet(@RequestParam String name) {
        return greetingCounterService.greet(name);
    }
}
