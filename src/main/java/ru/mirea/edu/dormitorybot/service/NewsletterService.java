package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.basic.Message;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.student.JpaStudentService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterService {
    private final VkBotService vkBotService;
    private final JpaStudentService studentService;

    private static final int PARTITION_SIZE = 100;

    public void sendNewsLetterForEveryone(Message message) {
        log.info("Отправка сообщения всем пользователям...   {}", message);
        List<Integer> ids = studentService.getStudents();
        for (List<Integer> hundredPartition: Lists.partition(ids, PARTITION_SIZE)) {
            vkBotService.sendTextMessageForManyUsers(hundredPartition, message.getText());
        }
    }
}
