package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.media.Photo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import ru.mirea.edu.dormitorybot.statemachine.State;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleImageService scheduleService;
    private final VkBotService vkBotService;

    public void sendSchedule(Integer id) {
        try {
            InputStream scheduleInputStream = scheduleService.getSchedule();
            vkBotService.sendTextWithImage(id, "Расписание", scheduleInputStream);
        } catch (Exception e) {
            vkBotService.sendTextMessage(id, "Не удалось получить расписание, попробуйте позднее");
        }
    }

    public void updateSchedule(Integer id, String photoUrl) {
        try (InputStream photoStream = URI.create(photoUrl).toURL().openStream()) {
            scheduleService.updateSchedule(photoStream);
            vkBotService.sendTextMessage(id, "Расписание обновлено!");
        } catch (IOException e) {
            log.error("Error updating photo! {}", e.getMessage());
        }
    }

    public void askForPhoto(Integer id) {
        Keyboard keyboard = VkBotService.createKeyboard(List.of(Event.CANCEL.toString()));
        vkBotService.sendTextMessageWithKeyboard(id, "Пришлите фотографию расписания", keyboard);
    }

    public static String getPhotoUrlFromMessage(Message message) {
        Photo photo = message.getAttachments().getFirst().getPhoto();
        return photo.getPhotoSizes().getFirst().getSrc();
    }

    public static boolean checkMessageHasPhotoAttachment(Message message) {
        return message.getAttachments().size() == 1 && message.getAttachments().getFirst().getPhoto() != null;
    }
}
