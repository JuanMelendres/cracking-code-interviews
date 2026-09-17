package demo;

/** A related entity, kept as a plain class (no JPA annotations needed for
 * this demo -- the persistence lifecycle itself is already covered in
 * jpa-entity-lifecycle-and-the-n1-problem.md; this pack is only about the
 * Entity/DTO/Mapper relationship). */
public class Customer {
    private Long id;
    private String name;

    public Customer(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
