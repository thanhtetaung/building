package com.flextech.building.aws.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

import java.net.URI;

@ConfigurationProperties(prefix = "aws.s3")
@Data
public class S3ClientConfigurationProperties {
    private Region region = Region.AP_NORTHEAST_1;
    private URI endpoint = null;

    private String accessKeyId;
    private String secretAccessKey;

    // Bucket name we'll be using as our backend storage
    private String bucket;

    private String resultBucket;

    // AWS S3 requires that file parts must have at least 5MB, except
    // for the last part. This may change for other S3-compatible services, so let't
    // define a configuration property for that
    private int multipartMinPartSize = 5*1024*1024;
}
