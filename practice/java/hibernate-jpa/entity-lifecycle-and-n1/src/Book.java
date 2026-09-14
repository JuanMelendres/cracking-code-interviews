import jakarta.persistence.*;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // LAZY is NOT the JPA default for @ManyToOne (EAGER is) -- set explicitly
    // here since a lazy-loaded parent reference is the far more common real
    // production choice, and this chapter's demos are about LAZY specifically.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    protected Book() {} // required by JPA

    public Book(String title) { this.title = title; }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
    public void setTitle(String title) { this.title = title; }
}
