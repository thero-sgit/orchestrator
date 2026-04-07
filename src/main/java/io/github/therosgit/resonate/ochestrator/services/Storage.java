package io.github.therosgit.resonate.ochestrator.services;

import software.amazon.awssdk.services.s3.model.ListObjectsResponse;

public interface Storage {
    void upload(String bucket, String key, byte[] data);
    ListObjectsResponse listObjects(String bucket);
    byte[] download(String bucketName, String objectName);
}
