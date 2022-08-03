package com.github.dagurasu.quarkussocial.rest;

import com.github.dagurasu.quarkussocial.domain.model.Follower;
import com.github.dagurasu.quarkussocial.domain.model.User;
import com.github.dagurasu.quarkussocial.domain.repository.FollowerRepository;
import com.github.dagurasu.quarkussocial.domain.repository.UserRepository;
import com.github.dagurasu.quarkussocial.rest.dto.FollowerRequest;
import com.github.dagurasu.quarkussocial.rest.dto.ResponseError;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    public void setUp(){

        // standard user tests
        var user = new User();
        user.setAge(28);
        user.setName("Douglas");
        userRepository.persist(user);
        userId = user.getId();

        // follower tests
        var follower = new User();
        follower.setAge(28);
        follower.setName("Théo");
        userRepository.persist(follower);
        followerId = follower.getId();

        // create a follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    @Test
    @DisplayName("should return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest(){

        var body  = new FollowerRequest();
        body.setFollowerId(userId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParams("userId", userId)
            .when()
            .put()
            .then()
            .statusCode(Response.Status.CONFLICT.getStatusCode())
            .body(Matchers.is("You can't follow yourself!"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when userId doesn't exists")
    public void userNotFoundWhenTryingToFollowTest(){

        var body  = new FollowerRequest();
        body.setFollowerId(userId);

        var inexistentUserId  =999;
        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParams("userId", inexistentUserId)
            .when()
            .put()
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should follow a user")
    public void followUserTest(){

        var body  = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .pathParams("userId", userId)
            .when()
            .put()
            .then()
            .statusCode(204);
    }

    @Test
    @DisplayName("should return 404 on list user followers and userId doesn't exists")
    public void userNotFoundWhenListingFollowTest(){

        var inexistentUserId  =999;
        given()
            .contentType(ContentType.JSON)
            .pathParams("userId", inexistentUserId)
            .when()
            .get()
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should list an users followers")
    public void listFollowersTest(){

       var response =  given()
            .contentType(ContentType.JSON)
            .pathParams("userId", userId)
            .when()
            .get()
            .then()
            .extract()
           .response();

       var followersCount = response.jsonPath().get("followersCount");
       var followersContent = response.jsonPath().getList("content");

       assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
       assertEquals(1, followersCount);
       assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("should return 404 on unfollow user id doesn't exists")
    public void userNotFoundWhenUnfollowingAnUserTest(){

        var inexistentUserId  =999;
        given()
            .contentType(ContentType.JSON)
            .pathParams("userId", inexistentUserId)
            .queryParam("followerId", followerId)
            .when()
            .delete()
            .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("should unfollow an user")
    public void unfollowUserTest(){
        given()
            .pathParams("userId", userId)
            .queryParam("followerId", followerId)
            .when()
            .delete()
            .then()
            .statusCode(204);
    }
}