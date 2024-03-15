package ru.mirea.edu.dormitorybot;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.mirea.edu.dormitorybot.bot.VkBot;

@SpringBootApplication
public class DormitoryBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DormitoryBotApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(VkBot vkBot) {
        return args -> {
            vkBot.startPolling();
        };
    }
}
