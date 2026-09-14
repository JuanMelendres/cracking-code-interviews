package demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

// Contrast with GreetingControllerSliceTest: @SpringBootTest loads the FULL
// context, so the real GreetingService bean genuinely gets constructed here --
// no @MockBean needed, because it's actually present.
@SpringBootTest
public class SpringBootTestFullContextTest {

    @Autowired
    private GreetingService greetingService;

    @Test
    void fullContextLoadsTheRealGreetingServiceBean() {
        System.out.println("Real GreetingService instances created so far: "
                + GreetingService.getInstancesCreated());
        assertTrue(GreetingService.getInstancesCreated() >= 1);
    }
}
