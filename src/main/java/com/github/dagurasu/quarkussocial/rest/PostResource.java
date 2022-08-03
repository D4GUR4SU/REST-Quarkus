package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.domain.model.Post;
import com.github.dagurasu.quarkussocial.domain.model.User;
import com.github.dagurasu.quarkussocial.domain.repository.FollowerRepository;
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
    private FollowerRepository followerRepository;
    private UserRepository repository;

    @Inject
    public PostResource(
            UserRepository repository,
            PostRepository postRepository,
            FollowerRepository followerRepository){
        this.repository = repository;
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
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
    public Response listPosts(
            @PathParam("userId") Long id,
            @HeaderParam("followerId") Long followerId){

        User user = repository.findById(id);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("You can't see these posts!").build();
        }

        if(followerId == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId").build();
        }

        var follower = repository.findById(followerId);
        if(follower == null){
            return Response.status(Response.Status.BAD_REQUEST).entity("Inexistent followerId").build();
        }


        boolean follows = followerRepository.follows(follower, user);
        if(!follows){
            return Response.status(Response.Status.FORBIDDEN).build();
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
