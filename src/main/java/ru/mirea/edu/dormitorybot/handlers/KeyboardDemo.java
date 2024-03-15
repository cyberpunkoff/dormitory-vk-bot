package ru.mirea.edu.dormitorybot.handlers;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.events.Update;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.additional.buttons.Button;
import api.longpoll.bots.model.objects.additional.buttons.TextButton;
import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.media.AttachedLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class KeyboardDemo implements UpdateHandler {
    private final VkBotsMethods vk;

    @Override
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

        if (message.getText().equals("Расписание смены белья")) {
            File file = new File("5zeiHGHbAoY.jpg");
            vk.messages.send()
                    .setPeerIds(message.getPeerId())
                    .setMessage("Расписание на текущий месяц")
                    .addPhoto(file)
                    .execute();
        }

        Keyboard keyboard = new Keyboard(buttons).setOneTime(true);


        if (message.getText().equals("Расписание смены белья")) {
            File file = new File("5zeiHGHbAoY.jpg");
            vk.messages.send()
                    .setPeerIds(message.getPeerId())
                    .setMessage("Расписание на текущий месяц")
                    .addPhoto(file)
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


        vk.messages.send()
                .setPeerIds(message.getPeerId())
                .setMessage("Чем я могу вам помочь?")
                .setKeyboard(keyboard)
                .execute();
    }
}
