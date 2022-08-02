package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.domain.model.User;
import com.github.dagurasu.quarkussocial.domain.repository.UserRepository;
import com.github.dagurasu.quarkussocial.rest.dto.CreateUserRequest;
import com.github.dagurasu.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.transaction.Transactional;

import javax.validation.ConstraintViolation;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository repository;
    Validator validator;

    @Inject
    public UserResource(UserRepository repository, Validator validator){
        this.validator = validator;
        this.repository = repository;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) throws IOException, SAXException {

        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);
        if(!violations.isEmpty()){
            ResponseError responseError = ResponseError.createFromValidation(violations);
            return Response.status(400).entity(responseError).build();
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());
        repository.persist(user);

        return Response.ok(user).build();
    }

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> query = repository.findAll();
        return Response.ok(query.list()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = repository.findById(id);

        if (user != null){
            repository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest req){
        User user = repository.findById(id);

        if(user != null){
            user.setName(req.getName());
            user.setAge(req.getAge());
            return Response.ok(user).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
