package com.github.dagurasu.quarkussocial.rest.dto;

import com.github.dagurasu.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowersResponse {

    private Long id;
    private String name;

    public FollowersResponse() {

    }

    public FollowersResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public FollowersResponse(Follower follower) {
         this(follower.getId(), follower.getFollower().getName());
    }
}
