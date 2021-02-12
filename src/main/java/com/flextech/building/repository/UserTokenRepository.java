package com.flextech.building.repository;

import com.flextech.building.entity.User;
import com.flextech.building.entity.UserToken;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserTokenRepository extends ReactiveMongoRepository<UserToken, String> {

    public Mono<UserToken> findByToken(String token);

    public Mono<Void> deleteByUser(User user);
}
