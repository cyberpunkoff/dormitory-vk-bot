package ru.mirea.edu.dormitorybot.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app")
@Configuration
@Data
public class ApplicationConfig {
    String accessToken;
}
