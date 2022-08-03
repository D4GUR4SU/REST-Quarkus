package com.github.dagurasu.quarkussocial.domain.repository;

import com.github.dagurasu.quarkussocial.domain.model.Follower;
import com.github.dagurasu.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){
        var params = Parameters.with("follower", follower)
                .and("user", user);

        var query = find("follower = :follower and user = :user", params);
        var result = query.firstResultOptional();

        return result.isPresent();
    }
}
