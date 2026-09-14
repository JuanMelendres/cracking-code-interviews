package demo;

import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

// Stands in for a real downstream dependency (a database, a partner API) whose
// real availability should genuinely drive this application's own health.
@Component
public class DownstreamDependency {

    private final AtomicBoolean available = new AtomicBoolean(true);

    public boolean isAvailable() {
        return available.get();
    }

    public void setAvailable(boolean value) {
        available.set(value);
    }
}
