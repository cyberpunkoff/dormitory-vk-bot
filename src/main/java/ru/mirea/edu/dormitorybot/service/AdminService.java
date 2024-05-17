package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.basic.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.student.StudentService;
import ru.mirea.edu.dormitorybot.statemachine.Event;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final VkBotService vkBotService;
    private final VkBotsMethods vk;
    private final StudentService studentService;

    public void sendRequest(Integer id) {
        Keyboard keyboard = VkBotService.createKeyboard(List.of(Event.CANCEL.toString()));
        vkBotService.sendTextMessageWithKeyboard(
                id,
                "Хорошо, теперь пришлите id пользователя или перешлите мне любое его сообщение!",
                keyboard
        );
    }

    public void addAdmin(Message message) {
        try {
            Integer userId = message.getFromId();
            Integer newAdminId = getIdFromMessage(message);
            studentService.makeStudentAdmin(newAdminId);
            vkBotService.sendTextMessage(userId, "Добавили администратора!");
        } catch (Exception ex) {
            vkBotService.sendTextMessage(message.getFromId(), "Ошибка обработки запроса");
        }
    }

    public void deleteAdmin(Message message) {
        try {
            Integer userId = message.getFromId();
            Integer newAdminId = getIdFromMessage(message);
            studentService.deleteAdmin(newAdminId);
            vkBotService.sendTextMessage(userId, "Удалили администратора!");
        } catch (Exception ex) {
            vkBotService.sendTextMessage(message.getFromId(), "Ошибка обработки запроса");
        }
    }

    public Integer getIdFromMessage(Message message) throws VkApiException {
        if (message.hasFwdMessages()) {
            return message.getFwdMessages().getFirst().getFromId();
        } else {
            var responseBody = vk.utils.resolveScreenName()
                    .setScreenName(message.getText())
                    .execute();
            return responseBody.getResponse().getObjectId();
        }
    }
}
