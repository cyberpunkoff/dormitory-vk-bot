package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.additional.Keyboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.student.StudentService;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import static ru.mirea.edu.dormitorybot.statemachine.Event.*;

import java.security.Key;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HelperService {
    private final StudentService studentService;
    private final VkBotService vkBotService;

    public void askForConfirmation(Integer id) {
        Keyboard keyboard = VkBotService.createKeyboard(List.of(Event.APPROVE.toString(), Event.CANCEL.toString()));
        vkBotService.sendTextMessageWithKeyboard(id, "Подтвердите обновление расписания", keyboard);
    }

    public void sendActionCanceledMessage(Integer id) {
        vkBotService.sendTextMessageWithKeyboard(id, "Действие отменено!", MenuService.MENU_KEYBOARD);
    }
}
