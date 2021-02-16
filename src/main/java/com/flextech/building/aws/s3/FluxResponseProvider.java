package com.flextech.building.aws.s3;

import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

public class FluxResponseProvider implements AsyncResponseTransformer<GetObjectResponse,FluxResponse> {
    private FluxResponse response;
    @Override
    public CompletableFuture<FluxResponse> prepare() {
        response = new FluxResponse();
        return response.getCompletableFuture();
    }

    @Override
    public void onResponse(GetObjectResponse sdkResponse) {
        this.response.setSdkResponse(sdkResponse);
    }

    @Override
    public void onStream(SdkPublisher<ByteBuffer> publisher) {
        response.setBufferFlux(Flux.from(publisher));
        response.getCompletableFuture().complete(response);
    }

    @Override
    public void exceptionOccurred(Throwable error) {
        response.getCompletableFuture().completeExceptionally(error);
    }
}
