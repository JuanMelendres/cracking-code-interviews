package demo;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class GreetingService {

    // Incremented in the constructor -- proof that Spring actually instantiated a
    // real bean of this type, distinguishing "the real service is in the context"
    // from "a Mockito stand-in is standing in for it."
    private static final AtomicInteger INSTANCES_CREATED = new AtomicInteger(0);

    public GreetingService() {
        INSTANCES_CREATED.incrementAndGet();
    }

    public static int getInstancesCreated() {
        return INSTANCES_CREATED.get();
    }

    public String greet(String name) {
        return "Hello, " + name;
    }
}
