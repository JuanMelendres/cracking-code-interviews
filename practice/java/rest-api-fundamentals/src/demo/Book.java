package demo;

public class Book {
    private Long id;
    private String title;
    private boolean available;

    public Book() {
    }

    public Book(Long id, String title, boolean available) {
        this.id = id;
        this.title = title;
        this.available = available;
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
}
