package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.basic.Message;
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

    public void sendNewsLetterForEveryone(Message message) {
        log.info("Отправка сообщения всем пользователям...   {}", message);
        List<Integer> ids = studentService.getStudents();
        vkBotService.sendTextMessageForManyUsers(ids, message.getText());
    }
}
