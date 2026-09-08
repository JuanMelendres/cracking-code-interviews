package demo;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

// The business-logic layer, sitting between the controller and the
// repository. This class never mentions HTTP at all -- it has no idea it's
// being called from a web request, which is the actual point of a layered
// design: this class is just as usable from a CLI tool or a test as from
// TaskController below.
@Service
public class TaskService {
    private final TaskRepository repository;

    // Constructor-based dependency injection: no @Autowired annotation is
    // needed here at all, because a class with exactly one constructor gets
    // it applied automatically (Spring Framework 4.3+). This IS dependency
    // injection: TaskService never calls `new TaskRepository()` itself --
    // Spring builds one TaskRepository bean and hands it in.
    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Task createTask(String title) {
        return repository.save(new Task(null, title, false));
    }

    public List<Task> listTasks() {
        return repository.findAll();
    }

    public Optional<Task> getTask(Long id) {
        return repository.findById(id);
    }
}
