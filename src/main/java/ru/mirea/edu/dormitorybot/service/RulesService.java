package ru.mirea.edu.dormitorybot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.minio.MinioService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class RulesService {
    private final VkBotService vkBotService;
    private final MinioService minioService;

    public void sendRules(Integer id) {
        InputStream file = minioService.getFile("schedule", "rules.pdf");
        vkBotService.sendTextWithDocument(id, "Распорядок", file);
    }

}
