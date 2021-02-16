package com.flextech.building.aws.s3;

import lombok.Data;
import reactor.core.publisher.Flux;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Data
public class FluxResponse {
    private final CompletableFuture<FluxResponse> completableFuture = new CompletableFuture<>();
    private GetObjectResponse sdkResponse;
    private Flux<ByteBuffer> bufferFlux;
}
