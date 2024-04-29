package ru.mirea.edu.dormitorybot.service.minio;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @SneakyThrows
    public InputStream getFile(String bucketName, String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    @SneakyThrows
    public StatObjectResponse getFileStats(String bucketName, String objectName) {
        return minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    @SneakyThrows // TODO: Тут точно это нужно? Или лучше по-хорошему обрабарывать?
    public void putFile(String bucketName, String objectName, InputStream stream) {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, -1, 10485760) // TODO: убрать вот эти маджики
                .build());
    }
}
