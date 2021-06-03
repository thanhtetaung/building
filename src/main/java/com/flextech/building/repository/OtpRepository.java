package com.flextech.building.repository;

import com.flextech.building.entity.Otp;
import com.flextech.building.entity.enums.Indicator;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OtpRepository extends ReactiveMongoRepository<Otp, String> {

    public Mono<Otp> findByOtpAndLinkId(String otp, String linkId);

    public Mono<Otp> findByOtpAndLinkIdAndUsedInd(String otp, String linkId, Indicator usedInd);

    public Mono<Void> deleteAllByLinkId(String linkId);

}
