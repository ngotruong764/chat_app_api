package com.usth.chat_app_api.constant;

import org.springframework.beans.factory.annotation.Value;

public final class ApplicationConstant {
    @Value("${amazon.s3.bucket-name}")
    public static String AWS_BUCKET_NAME = "store-image-chat-app";
//    public static String AWS_BUCKET_NAME = "first-s3-bucket-nqt";

    public static final String S3_PATH = "https://store-image-chat-app.s3.ap-southeast-1.amazonaws.com/";
    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "asdkjashdqrbvbsdfusid237982142134";
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_ISSUER = "Talkie";

    // Random token
    public static final int MIN = 100_000;
    public static final int MAX = 999_999;

    // Video Call
    public static final String VIDEO_CALL = "VIDEO";
    public static final String AUDIO_CALL = "AUDIO";
    public static final String AGREE_CALL = "Agree";
    public static final String DECLINE_CALL = "Decline";

    public static final String END_CALL = "END";
}
