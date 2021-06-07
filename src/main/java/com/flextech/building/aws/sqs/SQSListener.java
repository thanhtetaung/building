package com.flextech.building.aws.sqs;


import com.flextech.building.service.BuildingService;
import com.jashmore.sqs.argument.payload.Payload;
import com.jashmore.sqs.spring.container.basic.QueueListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.*;


@Component
@Slf4j
public class SQSListener {

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    @Autowired
    private BuildingService buildingService;


    private Mono<ReceiveMessageResponse> receiveMessageResponseMono;


    @QueueListener("${aws.sqs.queueUrl}")
    public void listen(@Payload String payload) {
        String executionArn = this.extractExecutionArc(payload);
        this.buildingService.updateResult(executionArn)
                .block();
    }

    private String extractExecutionArc(String body) {
        try {
            JSONObject json = new JSONObject(body);
            if (json.has("executionArn")) {
                return json.getString("executionArn");
            } else {
                throw new RuntimeException("Invalid message content");
            }

        } catch (JSONException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
