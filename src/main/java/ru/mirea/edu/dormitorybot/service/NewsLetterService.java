package ru.mirea.edu.dormitorybot.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsLetterService {
    private final VkBotService vkBotService;
    private final List<Integer> ids; //TODO SomeUserService

    private static final int PARTITION_SIZE = 100;

    public void sendNewsLetterForEveryone(String message) {
        for (List<Integer> hundredPartition: Lists.partition(ids, PARTITION_SIZE)) {
            vkBotService.sendTextMessageForManyUsers(hundredPartition, message);
        }
    }
}
