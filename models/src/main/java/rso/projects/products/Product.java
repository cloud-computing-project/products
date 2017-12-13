package rso.projects.products;

import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "products")
@NamedQueries(value =
        {
                @NamedQuery(name = "Product.getAll", query = "SELECT p FROM products p")
        })
@UuidGenerator(name = "idGenerator")
public class Product {

    @Id
    @GeneratedValue(generator = "idGenerator")
    private String id;

    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
