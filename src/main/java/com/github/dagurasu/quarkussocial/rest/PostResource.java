package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.domain.model.Post;
import com.github.dagurasu.quarkussocial.domain.model.User;
import com.github.dagurasu.quarkussocial.domain.repository.PostRepository;
import com.github.dagurasu.quarkussocial.domain.repository.UserRepository;
import com.github.dagurasu.quarkussocial.rest.dto.CreatePostRequest;
import com.github.dagurasu.quarkussocial.rest.dto.PostResponse;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private PostRepository postRepository;
    private UserRepository repository;

    @Inject
    public PostResource(UserRepository repository, PostRepository postRepository){
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long id, CreatePostRequest request){
        User user = repository.findById(id);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post posts = new Post();
        posts.setText(request.getText());
        posts.setUser(user);

        postRepository.persist(posts);
        return Response.status(Response.Status.CREATED).entity(request).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long id){
        User user = repository.findById(id);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var query = postRepository.find("user_id", Sort.by("date_time", Sort.Direction.Descending), user);
        var list = query.list();

        var postResponse = list
                .stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponse).build();
    }
}
