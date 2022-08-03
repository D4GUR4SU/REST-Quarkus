package com.github.dagurasu.quarkussocial.domain.repository;

import com.github.dagurasu.quarkussocial.domain.model.Follower;
import com.github.dagurasu.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.PathParam;
import java.util.List;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user){
        var params = Parameters.with("follower", follower)
                .and("user", user);

        var query = find("follower = :follower and user = :user", params);
        var result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId){
        var query = find("user.id", userId);
        var list = query.list();
        return list;
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {

        var params = Parameters.with("userId", userId).and("followerId", followerId);
        delete("follower.id =:followerId and user.id =:userId", params);
    }
}
