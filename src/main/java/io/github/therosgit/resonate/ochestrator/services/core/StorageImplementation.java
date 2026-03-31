package io.github.therosgit.resonate.ochestrator.services.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class StorageImplementation implements io.github.therosgit.resonate.ochestrator.services.Storage {
    private S3Client s3Client;

    public StorageImplementation(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void upload(String bucket, String key, byte[] data) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(data)
        );
    }

    public ListObjectsResponse listObjects(String bucket) {
        return s3Client.listObjects(
                ListObjectsRequest.builder()
                        .bucket(bucket)
                        .build()
        );
    }
}
