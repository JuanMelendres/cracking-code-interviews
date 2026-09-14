package demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

// The persistence layer -- in-memory here, deliberately, so the chapter's
// point (the CONTRACT between layers, not a real database) stays isolated.
// @Repository marks this as a Spring-managed bean, same mechanism as
// @Service and @Controller below; Spring gives it no special runtime
// behavior beyond that (no ORM machinery is involved here at all).
@Repository
public class TaskRepository {
    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Task save(Task task) {
        task.setId(nextId.getAndIncrement());
        tasks.add(task);
        return task;
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Optional<Task> findById(Long id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }
}
