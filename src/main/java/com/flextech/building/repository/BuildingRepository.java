package com.flextech.building.repository;

import com.flextech.building.entity.Building;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BuildingRepository extends ReactiveMongoRepository<Building, String> {

    public Flux<Building> findAllByUserIdOrderByLastUpdateDateDesc(String userId);

    public Mono<Building> findByIdAndUserId(String id, String userId);

    public Mono<Building> findByExecutionArnAndStartDate(String executionArn, Double startDate);

    public Mono<Building> findByExecutionArn(String executionArn);

}
