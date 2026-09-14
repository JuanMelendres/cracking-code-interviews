package demo;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Book {
    private Long id;
    private String title;
    private boolean available;
    // Optional, client-supplied unique business key (distinct from the
    // server-generated id) -- @JsonInclude keeps it out of the JSON entirely
    // when null, so every existing curl-transcript.txt output (which never
    // sends isbn) stays byte-for-byte unchanged.
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String isbn;

    public Book() {
    }

    public Book(Long id, String title, boolean available) {
        this.id = id;
        this.title = title;
        this.available = available;
    }

    public Book(Long id, String title, boolean available, String isbn) {
        this.id = id;
        this.title = title;
        this.available = available;
        this.isbn = isbn;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
