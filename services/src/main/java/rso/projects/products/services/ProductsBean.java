package rso.projects.products.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.projects.products.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
public class ProductsBean {

    @Inject
    private EntityManager em;

    public List<Product> getProducts(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Product.class, queryParameters);

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
