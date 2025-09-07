package com.dockeep.s3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@Getter
@RequiredArgsConstructor
public class S3Service {
    private static final long SIGNATURE_DURATION_MINUTES = 10;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String s3Key = UUID.randomUUID() + "." + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .serverSideEncryption(ServerSideEncryption.AES256) // Eventually,
                // client-side encryption will be necessary.
                .contentType(file.getContentType())
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());
        s3Client.putObject(putObjectRequest, requestBody);

        return s3Key;
    }

    public String generatePutPresignedURL(PutObjectRequest putObjectRequest){
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest =
                s3Presigner.presignPutObject(putObjectPresignRequest);

        return presignedPutObjectRequest.url().toString();
    }

    public String generateGetPresignedURL(String s3Key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        GetObjectPresignRequest objectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION_MINUTES))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(objectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

}

/**
 *
 *      MISTAKES TO AVOID
 *
 *
 * public void uploadFile(MultipartFile file) throws IOException {
 *      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
 *              .bucket(bucketName)
 *              .key(file.getOriginalFileName()) // Possible naming collisions.
 *              .contentType(file.getContentType())
 *              .build();
 *
 *
 *     RequestBody requestBody =
 *                  RequestBody.fromBytes(file.getBytes()); // Loads entire file into memory before sending
 *                                                          // BAD FOR LARGE FILES.
 *                                                          // getInputStream() => binary info delivered progressively instead of all at once.
 *     s3Client.putObject(putObjectRequest, requestBody);
 * }
 *
 *
 *      */