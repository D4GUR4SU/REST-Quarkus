package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.domain.model.Follower;
import com.github.dagurasu.quarkussocial.domain.model.Post;
import com.github.dagurasu.quarkussocial.domain.model.User;
import com.github.dagurasu.quarkussocial.domain.repository.FollowerRepository;
import com.github.dagurasu.quarkussocial.domain.repository.PostRepository;
import com.github.dagurasu.quarkussocial.domain.repository.UserRepository;
import com.github.dagurasu.quarkussocial.rest.dto.CreatePostRequest;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject

    FollowerRepository followerRepository;

    @Inject
    PostRepository postRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUp(){
        // standard user tests
        var user = new User();
        user.setAge(28);
        user.setName("Douglas");
        userRepository.persist(user);
        userId = user.getId();

        // create a new post
        var post = new Post();
        post.setText("Hello World");
        post.setUser(user);
        postRepository.persist(post);

        // user follows anybody
        var userNotFollower = new User();
        userNotFollower.setAge(95);
        userNotFollower.setName("Idoso");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // user follows somebody
        var userFollower = new User();
        userFollower.setAge(1000);
        userFollower.setName("Museu");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        var follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for user")
    public void createPostTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParams("userId", userId)
            .when()
            .post()
            .then()
            .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for inexistent user")
    public void postForInexistentUserTest(){
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 999;
        given()
            .contentType(ContentType.JSON)
            .body(postRequest)
            .pathParams("userId", inexistentUserId)
            .when()
            .post()
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exists")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 9999;
        given()
            .pathParams("userId", inexistentUserId)
            .when()
            .get()
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when follower id header is not present")
    public void listPostFollowerHeaderNotSendTest(){
        given().pathParams("userId", userId)
            .when()
            .get()
            .then()
            .statusCode(400)
            .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exists")
    public void listPostFollowerNotFoundTest(){
        var inexistentFollowerId = 999;
        given()
            .pathParams("userId", userId)
            .header("followerId", inexistentFollowerId)
            .when()
            .get()
            .then()
            .statusCode(400)
            .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower")
    public void listPostNotAFollowerTest() {
        given()
            .pathParams("userId", userId)
            .header("followerId", userNotFollowerId)
            .when()
            .get()
            .then()
            .statusCode(403)
            .body(Matchers.is("You can't see these posts!"));

    }

    @Test
    @DisplayName("should return posts")
    public void listPostTest(){

        given()
            .pathParams("userId", userId)
            .header("followerId", userFollowerId)
            .when()
            .get()
            .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }

}