package com.usth.chat_app_api.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsConfig {
    // Inject access key from application.properties
    @Value("${amazon.s3.access-key}")
    private String accessKey;

    // Inject secret key from application.properties
    @Value("${amazone.s3.secret-key}")
    private String secretKey;

    // Inject region from application.properties
    @Value("${amazon.s3.region.static}")
    private String region;

    // Creating bean for S3 client
    @Bean
    public S3Client s3Client(){
        // Creating AWS credential using access key and secret key
        AwsCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        // Building S3 client with specific accessKey, secretKey and region
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
