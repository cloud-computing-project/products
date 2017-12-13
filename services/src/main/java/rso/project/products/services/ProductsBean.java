package rso.project.products.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import rso.project.products.models.Product;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.List;

@ApplicationScoped
public class ProductsBean {

    @Inject
    private EntityManager em;

    @Inject
    private ProductsBean productsBean;

    private Client httpClient;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<Product> getProducts() {

        TypedQuery<Product> query = em.createNamedQuery("Product.getAll", Product.class);

        return query.getResultList();
    }

    public Product getProduct(String productId) {

        Product product = em.find(Product.class, productId);

        if (product == null) {
            throw new NotFoundException();
        }

        return product;
    }

    public Product createProduct(Product product) {

        try {
            beginTx();
            em.persist(product);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return product;
    }

    public Product putProduct(String productId, Product product) {

        Product p = em.find(Product.class, productId);

        if (p == null) {
            return null;
        }

        try {
            beginTx();
            product.setId(p.getId());
            product = em.merge(product);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return product;
    }

    public boolean deleteProduct(String productId) {

        Product product = em.find(Product.class, productId);

        if (product != null) {
            try {
                beginTx();
                em.remove(product);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
