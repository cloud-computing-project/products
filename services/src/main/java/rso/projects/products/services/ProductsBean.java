package rso.projects.products.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import rso.projects.products.Order;
import rso.projects.products.Product;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import rso.projects.products.Sale;
import rso.projects.products.Shipping;
import rso.projects.products.services.config.RestProperties;

@ApplicationScoped
public class ProductsBean {

    @Inject
    private EntityManager em;

    @Inject
    private RestProperties restProperties;

    @Inject
    private ProductsBean productsBean;

    private Client httpClient;

    @Inject
    @DiscoverService("sales")
    private Optional<String> baseUrl;

    @Inject
    @DiscoverService("shippings")
    private Optional<String> baseUrlShip;

    @Inject
    @DiscoverService("orders")
    private Optional<String> baseUrlOrder;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
        //baseUrl = "http://192.168.99.100:8081"; // only for demonstration
    }

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

        if (restProperties.isOrderServiceEnabled()) {
            List<Sale> sales = productsBean.getSales(productId);
            List<Shipping> shippings = productsBean.getShippings(productId);
            List<Order> orders = productsBean.getOrders(productId);
            product.setSales(sales);
            product.setShippings(shippings);
            product.setOrders(orders);
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

    @CircuitBreaker(requestVolumeThreshold = 2)
    @Fallback(fallbackMethod = "getSalesFallback")
    @Timeout
    public List<Sale> getSales(String productId) {

        if (baseUrl.isPresent()) {

            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/sales?where=productId:EQ:" + productId)
                        .request().get(new GenericType<List<Sale>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                System.out.println("error "+e);
                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<>();

    }

    public List<Shipping> getShippings(String productId) {

        if (baseUrlShip.isPresent()) {

            try {
                return httpClient
                        .target(baseUrlShip.get() + "/v1/shippings?where=productId:EQ:" + productId)
                        .request().get(new GenericType<List<Shipping>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                System.out.println("error "+e);
                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<>();

    }

    public List<Order> getOrders(String productId) {

        if (baseUrlOrder.isPresent()) {

            try {
                return httpClient
                        .target(baseUrlOrder.get() + "/v1/orders?where=productId:EQ:" + productId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                System.out.println("error "+e);
                throw new InternalServerErrorException(e);
            }
        }

        return new ArrayList<>();

    }

    public List<Sale> getSalesFallback(String productId) {

        List<Sale> sales = new ArrayList<>();

        Sale sale = new Sale();

        sale.setOldPrice("N/A");
        sale.setNewPrice("N/A");

        sales.add(sale);

        return sales;

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
