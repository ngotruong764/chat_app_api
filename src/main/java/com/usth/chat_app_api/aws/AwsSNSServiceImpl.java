package com.usth.chat_app_api.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointRequest;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;


@Service
public class AwsSNSServiceImpl implements IAwsSNSService{
    @Autowired
    SnsClient snsClient;

    // Inject AWS Platform ARN
    @Value("${amazone.sns.arn}")
    private String platformArn;


    @Override
    public void publishNotification(String deviceToken, String title, String body) {
        // prepare request
        CreatePlatformEndpointRequest endpointRequest = CreatePlatformEndpointRequest.builder()
                .token(deviceToken)
                .platformApplicationArn(platformArn)
                .build();
        // send request
        CreatePlatformEndpointResponse endpointResponse = snsClient.createPlatformEndpoint(endpointRequest);

        // create message data
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode messageData = objectMapper.createObjectNode();
        messageData.put("title", title);
        messageData.put("body", body);

        ObjectNode defaultNode = objectMapper.createObjectNode();
        defaultNode.put("default", messageData.toString());

        // prepare notification request
        PublishRequest publishRequest = PublishRequest.builder()
                .message(defaultNode.toString())
                .messageStructure("json")
                .targetArn(endpointResponse.endpointArn())
                .build();

        // publish notification
        snsClient.publish(publishRequest);
    }
}
