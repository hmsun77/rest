package com.api.resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.api.model.Journal;
import com.api.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

@Component
@Path("/journals")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@Resource
public class JournalResource {

    @Autowired
    private JournalRepository repository;

    @Autowired
    private Journal journal;

    @Autowired
    private StringRedisTemplate cache;

    final String CACHE_PREFIX = "Q_";

    @GET
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public List<Journal> getAll() {
        return repository.findAll();
    }

    @GET
    @Path("{id}")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public Response get(@PathParam("id") String id) throws Exception {
        journal = repository.findOne(id);
        return (journal == null) ? Response.status(Response.Status.NOT_FOUND).build() : Response.ok(journal).build();
    }

    @POST
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public Response create(@Valid Journal inJournal) throws Exception {
        inJournal.setId(null);
        inJournal.setUpdated(new Date());
        journal = repository.insert(inJournal);
        if (journal == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else {
            return Response.ok(journal).status(Response.Status.CREATED).build();
        }
    }

    @PUT
    @Path("{id}")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public Response update(@PathParam("id") String id, @Valid Journal inJournal) throws Exception {
        journal = repository.findOne(id);
        if (journal == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            inJournal.setId(id);
            inJournal.setCreated(journal.getCreated());
            inJournal.setUpdated(new Date());
            repository.save(inJournal);
            return Response.noContent().build();
        }
    }

    @DELETE
    @Path("{id}")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public Response delete(@PathParam("id") String id) throws Exception {
        if (repository.findOne(id) == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            repository.delete(id);
            return Response.noContent().build();
        }
    }

    @GET
    @Path("suggest")
    @JacksonFeatures(serializationEnable = SerializationFeature.WRAP_ROOT_VALUE, deserializationEnable = DeserializationFeature.UNWRAP_ROOT_VALUE)
    public Response suggest(@QueryParam("query") String query) throws Exception {
        final String value = cache.opsForValue().get(CACHE_PREFIX + query); //look cache first
        if (value == null) {
            List<Journal> journalList = repository.findByTitleLike(query); //look in DB if not found in cache
            if (journalList == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            } else {
                final ObjectMapper mapper = new ObjectMapper();
                cache.opsForValue().set(CACHE_PREFIX + query, mapper.writeValueAsString(journalList)); //put in cache once found
                return Response.ok(journalList).build();
            }
        } else {
            return Response.ok(value).build();
        }
    }

}
