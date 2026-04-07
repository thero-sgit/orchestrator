package io.github.therosgit.resonate.ochestrator.services.core;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Service
public class StorageImplementation implements io.github.therosgit.resonate.ochestrator.services.Storage {
    private S3Client s3Client;

    public StorageImplementation(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void upload(String bucket, String key, byte[] data) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(data)
        );
    }

    @Override
    public ListObjectsResponse listObjects(String bucket) {
        return s3Client.listObjects(
                ListObjectsRequest.builder()
                        .bucket(bucket)
                        .build()
        );
    }

    @Override
    public byte[] download(String bucketName, String objectName) {
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectName)
                        .build()
        );

        return response.asByteArray();
    }
}
