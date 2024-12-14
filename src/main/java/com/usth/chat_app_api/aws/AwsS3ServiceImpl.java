package com.usth.chat_app_api.aws;

import com.usth.chat_app_api.utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.*;

@Slf4j
@Service
public class AwsS3ServiceImpl implements IAwsS3Service {
    @Autowired
    private S3Client s3Client;
    @Override
    public boolean uploadObject(String bucketName, String keyName,
                              Long contentLength, String contentType, String base64) {
        try{
            byte[] bytes = Base64.getDecoder().decode(base64);
            if(!Helper.isValidImg(bytes)){
                return false;
            }
            // create meta data
            Map<String, String> metadata = new HashMap<>();
            metadata.put("Content-Type", contentType);
            metadata.put("Content-Length", contentLength.toString());
            // create request to put to S3 bucket
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .metadata(metadata)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            log.info("File uploaded to bucket {}: keyName {}", bucketName, keyName);
        } catch (Exception e){
            log.info("File upload exception: {}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public byte[] downLoadObject(String bucketName, String keyName) {
        try{
            GetObjectRequest request = GetObjectRequest.builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> s3Object = s3Client.getObject(request, ResponseTransformer.toBytes());
            return s3Object.asByteArray();
        } catch (Exception e){
            log.info(e.getMessage());
            return new byte[0]; // create fixed empty byte[]
        }
    }

//    @Override
//    public boolean deleteFile(String bucketName, String keyName) {
////        try{
////            s3Client.deleteObject(bucketName, keyName);
////            log.info("File deleted from bucket({}): {}", bucketName, keyName);
////        } catch (AmazonClientException e){
////            log.info("S3 delete exception: {}", e.getMessage());
////            return false;
////        }
//        return true;
//    }
}
