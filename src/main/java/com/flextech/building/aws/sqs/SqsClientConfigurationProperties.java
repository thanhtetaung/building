package com.flextech.building.aws.sqs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;


@ConfigurationProperties(prefix = "aws.sqs")
@Data
public class SqsClientConfigurationProperties {
    private Region region = Region.AP_NORTHEAST_1;

    private String accessKeyId;

    private String secretAccessKey;

}
