package com.usth.chat_app_api.aws;

public interface IAwsSNSService {

    void publishNotification(String deviceToken, String title, String body, Long conversationId);
}
