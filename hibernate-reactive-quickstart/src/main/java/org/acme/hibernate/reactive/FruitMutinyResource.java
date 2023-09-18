package org.acme.hibernate.reactive;

import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import org.hibernate.reactive.mutiny.Mutiny.SessionFactory;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;

@Path("fruits")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FruitMutinyResource {

    private static final Logger LOG = Logger.getLogger(FruitMutinyResource.class);

    @Context
    HttpServerRequest serverRequest;

    @Inject
    SessionFactory sf;
    @Inject
    TranslationService translationService;

    @GET
    public Uni<List<LocalizedFruit>> get() {

        LOG.infof("Lang param %s", serverRequest.getParam("lang"));

        return sf.withTransaction((s,t) -> s
            .createNamedQuery("Fruits.findAll", Fruit.class)
            .getResultList()
            .chain(fruits ->
                translationService.getLocalizedEntities(
                    Fruit.class, fruits, LocalizedFruit::new)
            )
        );
    }

    @GET
    @Path("{id}")
    public Uni<Fruit> getSingle(Integer id) {
        return sf.withTransaction((s,t) -> s
            .find(Fruit.class, id)
            .chain(fruit -> translationService
                .getLocalizedEntity(Fruit.class, fruit, LocalizedFruit::new)
            )
        );
    }

    @POST
    public Uni<Response> create(Fruit fruit) {
        if (fruit == null || fruit.getId() != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        return sf.withTransaction((s,t) -> s.persist(fruit))
            .replaceWith(Response.ok(fruit).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Integer id, Fruit fruit) {
        if (fruit == null || fruit.getName() == null) {
            throw new WebApplicationException("Fruit name was not set on request.", 422);
        }

        return sf.withTransaction((s,t) -> s.find(Fruit.class, id)
            .onItem().ifNull().failWith(new WebApplicationException("Fruit missing from database.", NOT_FOUND))
                // If entity exists then update it
                .invoke(entity -> entity.setName(fruit.getName()))
                .map(entity -> Response.ok(entity).build()));
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Integer id) {
        return sf.withTransaction((s,t) -> s.find(Fruit.class, id)
                .onItem().ifNull().failWith(new WebApplicationException("Fruit missing from database.", NOT_FOUND))
                // If entity exists then delete it
                .call(s::remove))
                .replaceWith(Response.ok().status(NO_CONTENT)::build);
    }

    @POST
    @Path("/{id}/addTranslation")
    public Uni<Void> addTranslation(Integer id, TranslationDTO dto) {
        return translationService.addTranslation(
            Fruit.class, id, dto.getLanguage(), dto.getKey(), dto.getValue());
    }

    @DELETE
    @Path("/{id}/deleteTranslation")
    public Uni<Void> addTranslation(Integer id, TranslationKeyDTO dto) {
        return translationService.deleteTranslation(
            Fruit.class, id, dto.getLanguage(), dto.getKey());
    }

}
