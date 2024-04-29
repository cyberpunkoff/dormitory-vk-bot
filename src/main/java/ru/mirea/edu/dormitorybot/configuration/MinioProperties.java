package ru.mirea.edu.dormitorybot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "minio", ignoreUnknownFields = false)
@Configuration
@Data
public class MinioProperties {
    private String bucket;
    private String url;
    private String accessKey;
    private String secretKey;
}
