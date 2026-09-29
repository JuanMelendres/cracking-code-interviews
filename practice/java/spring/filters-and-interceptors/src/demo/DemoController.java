package demo;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DemoController {

    @GetMapping("/ok")
    Map<String, String> ok() {
        RequestTrace.record("Controller#ok", "handler body running");
        return Map.of("result", "ok");
    }

    @GetMapping("/boom-controller")
    Map<String, String> boomController() {
        RequestTrace.record("Controller#boomController", "about to throw DemoException");
        throw new DemoException("deliberate failure inside the controller");
    }

    @GetMapping("/boom-filter")
    Map<String, String> boomFilter() {
        // Never reached: the inner filter throws before the DispatcherServlet
        // is entered. Present only to prove the route itself is mapped, so the
        // 500 cannot be blamed on a missing handler.
        RequestTrace.record("Controller#boomFilter", "REACHED -- this line should never appear");
        return Map.of("result", "unreachable");
    }

    @GetMapping("/blocked")
    Map<String, String> blocked() {
        RequestTrace.record("Controller#blocked", "REACHED -- this line should never appear");
        return Map.of("result", "unreachable");
    }
}
