package demo;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// The web layer -- the ONLY class in this demo that knows HTTP exists at
// all. @RestController = @Controller + @ResponseBody: every method's return
// value is written directly to the HTTP response body (as JSON, via
// Jackson), instead of being resolved to a view template name.
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    // Same constructor-injection mechanism as TaskService -> TaskRepository
    // above -- the controller doesn't know or care that TaskService itself
    // has a dependency; Spring resolves the whole chain.
    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> listTasks() {
        return service.listTasks();
    }

    @PostMapping
    public Task createTask(@RequestBody Task request) {
        // @RequestBody: Jackson deserializes the incoming JSON request body
        // directly into this Task object before the method body even runs.
        return service.createTask(request.getTitle());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable("id") Long id) {
        // @PathVariable("id"): the {id} segment of the URL is bound directly
        // to this method parameter -- GET /tasks/3 binds id=3L. The name is
        // given explicitly here on purpose: without it, Spring falls back to
        // reading the parameter name from bytecode via reflection, which
        // requires compiling with the -parameters flag -- omitted here, and
        // the real failure this produces (a genuine IllegalArgumentException,
        // not a made-up one) is documented in Section 8's Common Mistakes.
        Optional<Task> task = service.getTask(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
