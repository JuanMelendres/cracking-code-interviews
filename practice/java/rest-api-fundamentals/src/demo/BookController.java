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

    @GetMapping("/latest")
    public ResponseEntity<Void> latestBook() {
        // 302 Found: a real, computed redirect -- "latest" is not itself a
        // resource, it is a pointer to whichever real book id is currently
        // highest. 302 (not 301) is deliberate: the target genuinely
        // changes over time as new books are created, so this is NOT a
        // permanent redirect a client should cache indefinitely.
        return repository.latestId()
                .map(id -> ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create("/books/" + id))
                        .<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book request) {
        // 422 Unprocessable Entity: the JSON is syntactically valid (it
        // parsed correctly -- otherwise Spring itself would already have
        // returned 400 before this method ever ran), but its CONTENT
        // violates a semantic business rule (a book must have a real
        // title). This is the real, practical distinction between 400 and
        // 422 that "both mean invalid request" glosses over.
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            return ResponseEntity.unprocessableEntity()
                    .body(new ErrorBody("title must not be blank"));
        }
        // 409 Conflict: a genuine data conflict on a real-world business
        // key (isbn), distinct from the server-generated id -- two books
        // MAY legitimately share a title (proved above), but not the same
        // isbn.
        if (request.getIsbn() != null && repository.existsByIsbn(request.getIsbn())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorBody("isbn " + request.getIsbn() + " already exists"));
        }
        // POST creates a NEW resource every time it's called -- it is
        // deliberately NOT idempotent. 201 Created (not 200 OK) is the
        // correct status for "a new resource now exists," and the Location
        // header points the client at the real URL of the resource just
        // created -- both are real REST conventions, not stylistic choices.
        Book created = repository.create(new Book(null, request.getTitle(), true, request.getIsbn()));
        URI location = URI.create("/books/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    // A minimal, real error-response body -- not the full, dedicated
    // exception-handling machinery Bean Validation and Global Exception
    // Handling (T-518) covers; that chapter's @RestControllerAdvice pattern
    // is the correct, scalable version of this same idea once a real app
    // has more than two ad hoc validation rules.
    static class ErrorBody {
        public String error;

        ErrorBody(String error) {
            this.error = error;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replaceBook(@PathVariable("id") Long id, @RequestBody Book request) {
        // PUT replaces an EXISTING resource's full state at a known id. It
        // IS idempotent by convention: calling it twice with the same body
        // leaves the resource in the same end state both times, unlike POST.
        // A real bug caught while building this pack's own 304 demo: this
        // line originally used the 3-arg constructor, silently dropping
        // isbn even when a PUT body explicitly included it -- exactly the
        // kind of accidental field-loss PUT's "full replacement" semantics
        // are supposed to prevent, not cause.
        Book toStore = new Book(id, request.getTitle(), request.isAvailable(), request.getIsbn());
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
