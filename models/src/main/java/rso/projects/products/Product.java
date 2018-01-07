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

    @Column(name = "manufacturer_id")
    private String manufacturerId;

    @Column(name = "category_id")
    private String categoryId;

    private String price;

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

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
