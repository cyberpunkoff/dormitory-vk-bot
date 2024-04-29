package ru.mirea.edu.dormitorybot.handlers;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.events.Update;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.additional.PhotoSize;
import api.longpoll.bots.model.objects.additional.buttons.Button;
import api.longpoll.bots.model.objects.additional.buttons.TextButton;
import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.media.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.mirea.edu.dormitorybot.exceptions.NoScheduleForCurrentMonthException;
import ru.mirea.edu.dormitorybot.service.minio.ScheduleImageService;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class KeyboardDemo implements UpdateHandler {
    private final VkBotsMethods vk;
    private final ScheduleImageService scheduleService;

    @Override
    @SneakyThrows
    public void handle(Update update) throws VkApiException {
        if (update.getType() != Update.Type.MESSAGE_NEW) {
            return;
        }

        Message message = ((MessageNew) update.getObject()).getMessage();

        List<List<Button>> buttons = List.of(
                List.of(new TextButton(Button.Color.SECONDARY, new TextButton.Action("Расписание смены белья"))),
                List.of(new TextButton(Button.Color.SECONDARY, new TextButton.Action("Администрация общежития"))),
                List.of(new TextButton(Button.Color.NEGATIVE, new TextButton.Action("Админ-панель")))
        );

        Keyboard keyboard = new Keyboard(buttons).setOneTime(true);

        if (message.getText().equals("Расписание смены белья")) {
            try {
                InputStream schedulePhotoStream = scheduleService.getSchedule();
                vk.messages.send()
                        .setPeerIds(message.getPeerId())
                        .setMessage("Расписание на текущий месяц")
                        .addPhoto("schedule.jpg", schedulePhotoStream)
                        .execute();
            } catch (NoScheduleForCurrentMonthException ignored) {
                vk.messages.send()
                        .setPeerIds(message.getPeerId())
                        .setMessage("Извините, расписание ещё не обновлено")
                        .execute();
            }
        }

        if (message.getText().equals("Админ-панель")) {
            List<List<Button>> adminButtons = List.of(
                    List.of(new TextButton(Button.Color.PRIMARY, new TextButton.Action("Обновить расписание"))),
                    List.of(new TextButton(Button.Color.PRIMARY, new TextButton.Action("Сделать рассылку")))
            );

            Keyboard adminKeyboard = new Keyboard(adminButtons).setOneTime(true);

            vk.messages.send()
                    .setPeerIds(message.getPeerId())
                    .setMessage("АДМИН-МЕНЮ")
                    .setKeyboard(adminKeyboard)
                    .execute();
            return;
        }

        if (message.getText().equals("Обновить расписание")) {
            InputStream photoStream = null;
            var attachments = message.getAttachments();
            for (Attachment attachment : attachments) {
                PhotoSize lastPhoto = attachment.getPhoto().getPhotoSizes().get(attachment.getPhoto().getPhotoSizes().size() - 1);
                String photoUrl = lastPhoto.getSrc();
                photoStream = URI.create(photoUrl).toURL().openStream();
            }
           scheduleService.updateSchedule(photoStream);
            return;
        }

        vk.messages.send()
                .setPeerIds(message.getPeerId())
                .setMessage("Чем я могу вам помочь?")
                .setKeyboard(keyboard)
                .execute();
    }
}
