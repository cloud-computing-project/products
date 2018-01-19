package rso.projects.products;

public class Rating {

    private String id;

    private String productId;

    private String rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName(){return productId;}

    public void setName(String name) {this.productId = productId;}

    public String getDescription(){return rating;}

    public void setDescription(String description){this.rating = rating;}

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
