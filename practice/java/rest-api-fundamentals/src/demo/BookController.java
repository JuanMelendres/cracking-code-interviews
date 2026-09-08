package demo;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Demonstrates the actual REST conventions this chapter teaches: resource
// nouns (never verbs) in the URL, one HTTP verb per operation, and a
// specific, meaningful status code per outcome -- not "200 for everything."
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookRepository repository;

    public BookController(BookRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Book> listBooks() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book request) {
        // POST creates a NEW resource every time it's called -- it is
        // deliberately NOT idempotent. 201 Created (not 200 OK) is the
        // correct status for "a new resource now exists," and the Location
        // header points the client at the real URL of the resource just
        // created -- both are real REST conventions, not stylistic choices.
        Book created = repository.create(new Book(null, request.getTitle(), true));
        URI location = URI.create("/books/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replaceBook(@PathVariable("id") Long id, @RequestBody Book request) {
        // PUT replaces an EXISTING resource's full state at a known id. It
        // IS idempotent by convention: calling it twice with the same body
        // leaves the resource in the same end state both times, unlike POST.
        Book toStore = new Book(id, request.getTitle(), request.isAvailable());
        boolean existed = repository.replace(id, toStore);
        if (!existed) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toStore);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("id") Long id) {
        boolean existed = repository.delete(id);
        if (!existed) {
            // A real, debatable design decision, not an oversight: this demo
            // returns 404 for deleting an already-gone resource. Some real
            // APIs instead return 204 again here, treating "already deleted"
            // as an equally successful idempotent outcome. Section 11
            // discusses both positions -- this demo picks one and states why.
            return ResponseEntity.notFound().build();
        }
        // 204 No Content: the operation succeeded and there is deliberately
        // no response body -- the resource no longer exists to describe.
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
