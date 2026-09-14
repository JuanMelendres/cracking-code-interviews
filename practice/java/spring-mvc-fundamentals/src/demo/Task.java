package demo;

// A plain data holder -- what a real request/response body actually maps to.
// Jackson (already on the classpath via spring-boot-starter's usual set,
// fetched explicitly here) converts this to/from JSON automatically because
// Spring registers a JSON message converter for any @RestController method.
public class Task {
    private Long id;
    private String title;
    private boolean done;

    public Task() {
        // required by Jackson to deserialize an incoming JSON request body
    }

    public Task(Long id, String title, boolean done) {
        this.id = id;
        this.title = title;
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
