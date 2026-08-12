import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // LAZY is the JPA default for @OneToMany -- this collection is NOT
    // fetched until something actually calls a method on it.
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    protected Author() {} // required by JPA

    public Author(String name) { this.name = name; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Book> getBooks() { return books; }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void setName(String name) { this.name = name; }
}
