package rso.projects.products.api.v1.resources;

import rso.projects.products.Product;
import rso.projects.products.services.ProductsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@ApplicationScoped
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductsResource {

    @Context
    private UriInfo uriInfo;

    @Inject
    private ProductsBean productsBean;

    @GET
    public Response getProducts() {

        List<Product> products = productsBean.getProducts(uriInfo);

        return Response.ok(products).build();
    }

    @GET
    @Path("/{productId}")
    public Response getProduct(@PathParam("productId") String productId) {

        Product product = productsBean.getProduct(productId);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(product).build();
    }

    @POST
    public Response createProduct(Product product) {

        if (product.getTitle() == null || product.getTitle().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            product = productsBean.createProduct(product);
        }

        if (product.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(product).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(product).build();
        }
    }

    @PUT
    @Path("{productId}")
    public Response putProduct(@PathParam("productId") String productId, Product product) {

        product = productsBean.putProduct(productId, product);

        if (product == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (product.getId() != null)
                return Response.status(Response.Status.OK).entity(product).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{productId}")
    public Response deleteProduct(@PathParam("productId") String productId) {

        boolean deleted = productsBean.deleteProduct(productId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
