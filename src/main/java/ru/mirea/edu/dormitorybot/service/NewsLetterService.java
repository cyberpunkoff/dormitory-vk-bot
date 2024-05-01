package ru.mirea.edu.dormitorybot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsLetterService {
    private final VkBotService vkBotService;
    private final List<Integer> ids; //TODO SomeUserService

    public void sendNewsLetterForEveryone(String message) {
        vkBotService.sendTextMessageForManyUsers(ids, message);
    }
}
