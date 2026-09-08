package demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository {
    private final Map<Long, Book> books = new LinkedHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Book create(Book book) {
        book.setId(nextId.getAndIncrement());
        books.put(book.getId(), book);
        return book;
    }

    public List<Book> findAll() {
        return List.copyOf(books.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    // Returns true if a book with this id existed and was replaced.
    public boolean replace(Long id, Book updated) {
        if (!books.containsKey(id)) {
            return false;
        }
        updated.setId(id);
        books.put(id, updated);
        return true;
    }

    // Returns true if a book with this id existed and was removed.
    public boolean delete(Long id) {
        return books.remove(id) != null;
    }
}
