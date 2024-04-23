package ru.mirea.edu.dormitorybot.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final MinioClient minioClient;

    @Value("${app.bucket}")
    private String bucket;
    @Value("${app.object}")
    private String object;

    @SneakyThrows
    public InputStream getSchedule() {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(object)
                        .build()
        );
    }

    @SneakyThrows
    public void putFile(InputStream stream) {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucket)
                .object(object)
                .stream(stream, -1, 10485760)
                .build());
    }

    @SneakyThrows
    public boolean isActual() {
        var stats = minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(bucket)
                .object(object)
                .build());
        var curMonth = OffsetDateTime.now().getMonth();
        return stats.lastModified().getMonth().equals(curMonth);
    }
}
