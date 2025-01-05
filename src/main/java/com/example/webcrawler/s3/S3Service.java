package com.example.webcrawler.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final Region region;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client, Region region) {
        this.s3Client = s3Client;
        this.region = region;
    }

    public String uploadContent(String content,String type) {
        String fileName = String.format("content-%s-%d.%s", type, Instant.now().toEpochMilli(), type);

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                        new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                        content.length()
                ));

        String s3Link = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), fileName);
        System.out.println("Uploaded content to S3: " + s3Link);
        return s3Link;
    }

    private String extractBucketName(String s3Url) {
        URI uri = URI.create(s3Url);
        String host = uri.getHost();
        return host.split("\\.")[0]; // Lấy phần trước ".s3"
    }

    private String extractKey(String s3Url) {
        URI uri = URI.create(s3Url);
        return uri.getPath().substring(1); // Bỏ dấu "/" ở đầu path
    }

    public String downloadContent(String s3Url) {
        // Tách bucketName và key từ URL
        String bucketName = extractBucketName(s3Url);
        String key = extractKey(s3Url);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        String content = new BufferedReader(new InputStreamReader(
                s3Client.getObject(getObjectRequest), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        System.out.println("Downloaded content from S3: " + s3Url);
        return content;
    }
}
