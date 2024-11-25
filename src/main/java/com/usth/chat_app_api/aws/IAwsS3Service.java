package com.usth.chat_app_api.aws;

public interface IAwsS3Service {
    /**
     * Method to upload file to an S3 bucket
     * @params
     *  bucketName: is the name of the bucket
     *  keyName: is the path that hold the file
     *  contentLength: length of the file (optional),
     *      if don't want to use, put contentLength = 0L
     *  contentType: type of the file (optional)
     *      if don't want to use, put contentType = ""
     *  base64: content of the file which is encoded to base64
     *
     */
    boolean uploadObject(
            final String bucketName,
            final String keyName,
            final Long contentLength,
            final String contentType,
            final String base64
    ) throws Exception;

    /**
     * Method to download object from S3
     * @param
     * bucketName: is the name of the bucket
     * keyName: is the path that hold the file
     */
    byte[] downLoadObject(
            final String bucketName,
            final String keyName
    );

    /**
     * Method to delete file from s3 bucket
     * @params
     *  bucketName: is the name of the bucket
     *  keyName: is the path that hold the file
     * */
//    boolean deleteFile(
//            final String bucketName,
//            final String keyName
//    )throws Exception;
}
