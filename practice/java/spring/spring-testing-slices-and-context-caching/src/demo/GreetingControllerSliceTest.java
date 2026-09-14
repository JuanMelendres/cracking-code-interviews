package demo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// FIXED: @MockBean supplies a Mockito stand-in for GreetingService into the
// slice's reduced ApplicationContext -- without this, the real
// NoSuchBeanDefinitionException below (captured verbatim in this pack's README)
// is what a slice test throws for any collaborator outside its slice.
//
// Real failure, captured before this fix was added:
//   org.springframework.beans.factory.UnsatisfiedDependencyException: Error
//   creating bean with name 'greetingController' ... Unsatisfied dependency
//   expressed through constructor parameter 0: No qualifying bean of type
//   'demo.GreetingService' available: expected at least 1 bean which
//   qualifies as autowire candidate.
@WebMvcTest(GreetingController.class)
public class GreetingControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GreetingService greetingService;

    @Test
    void greetEndpointReturnsGreeting() throws Exception {
        Mockito.when(greetingService.greet("Ada")).thenReturn("Hello, Ada");

        mockMvc.perform(get("/greet").param("name", "Ada"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, Ada"));
    }
}
