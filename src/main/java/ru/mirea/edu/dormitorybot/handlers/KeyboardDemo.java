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
import api.longpoll.bots.model.objects.media.Photo;
import io.minio.*;
import io.minio.messages.Item;
import io.minio.messages.ObjectLockConfiguration;
import io.minio.messages.Retention;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class KeyboardDemo implements UpdateHandler {
    private final VkBotsMethods vk;
    private final MinioClient minioClient;

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

//        if (message.getText().equals("Расписание смены белья")) {
//            File file = new File("5zeiHGHbAoY.jpg");
//            vk.messages.send()
//                    .setPeerIds(message.getPeerId())
//                    .setMessage("Расписание на текущий месяц")
//                    .addPhoto(file)
//                    .execute();
//        }

        Keyboard keyboard = new Keyboard(buttons).setOneTime(true);


        if (message.getText().equals("Расписание смены белья")) {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("schedule")
                            .object("schedule.jpg")
                            .build()
            );
            var stats = minioClient.statObject(StatObjectArgs.builder().bucket("schedule")
                    .object("schedule.jpg").build());
            if (!stats.lastModified().getMonth().equals(OffsetDateTime.now().getMonth())) {
                vk.messages.send()
                        .setPeerIds(message.getPeerId())
                        .setMessage("Извините, расписание ещё не обновлено")
                        .execute();
                return;
            }
            vk.messages.send()
                    .setPeerIds(message.getPeerId())
                    .setMessage("Расписание на текущий месяц")
                    .addPhoto("schedule.jpg", stream)
                    .execute();
            return;
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
                log.info(attachment.getPhoto());
                PhotoSize lastPhoto = attachment.getPhoto().getPhotoSizes().get(attachment.getPhoto().getPhotoSizes().size() - 1);
                String photoUrl = lastPhoto.getSrc();
                photoStream = URI.create(photoUrl).toURL().openStream();
            }
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("schedule")
                    .object("schedule.jpg")
                    .stream(photoStream, -1, 10485760)
                    .build());
            return;
        }

        vk.messages.send()
                .setPeerIds(message.getPeerId())
                .setMessage("Чем я могу вам помочь?")
                .setKeyboard(keyboard)
                .execute();
    }
}
