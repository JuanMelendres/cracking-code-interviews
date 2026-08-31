import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

// @Cacheable + @Cache opts this entity into the second-level cache -- a
// shared, cross-session cache, unlike the per-session first-level cache
// (the persistence context) already covered in the entity-lifecycle chapter.
@Entity
@jakarta.persistence.Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int stock;

    protected Product() {} // required by JPA

    public Product(String name, int stock) {
        this.name = name;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', stock=" + stock + "}";
    }
}
