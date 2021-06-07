package com.flextech.building.aws.sqs;

import com.flextech.building.aws.s3.S3ClientConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@EnableConfigurationProperties(SqsClientConfigurationProperties.class)
@Configuration
public class SQSConfig {



    @Bean
    public SqsAsyncClient amazonSQSAsyncClient(SqsClientConfigurationProperties props) {

        AwsCredentialsProvider credentialsProvider = () -> {
            AwsCredentials creds = AwsBasicCredentials.create(props.getAccessKeyId(), props.getSecretAccessKey());
            return creds;
        };
        return SqsAsyncClient.builder()
                .region(Region.AP_NORTHEAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }


}
