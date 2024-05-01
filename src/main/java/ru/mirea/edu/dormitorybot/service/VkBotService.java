package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.additional.buttons.Button;
import api.longpoll.bots.model.objects.additional.buttons.TextButton;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VkBotService {
    private final VkBotsMethods vk;

    @SneakyThrows
    public void sendTextMessage(Integer id, String text) {
        vk.messages.send()
                .setPeerIds(id)
                .setMessage(text)
                .execute();
    }

    @SneakyThrows
    public void sendTextMessageWithKeyboard(Integer id, String text, Keyboard keyboard) {
        vk.messages.send()
                .setPeerIds(id)
                .setMessage(text)
                .setKeyboard(keyboard)
                .execute();
    }

    @SneakyThrows
    public void sendTextWithImage(Integer id, String text, InputStream image) {
        vk.messages.send()
                .setPeerIds(id)
                .setMessage(text)
                .addPhoto("schedule.jpg", image)
                .execute();
    }

    public static Keyboard createKeyboard(List<String> labels) {
        List<List<Button>> buttons = new ArrayList<>();
        labels.forEach(label ->
                buttons.add(List.of(new TextButton(Button.Color.SECONDARY, new TextButton.Action(label))))
        );
        return new Keyboard(buttons);
    }

    @SneakyThrows
    public void sendTextMessageForManyUsers(List<Integer> ids, String text) {
        vk.messages.send()
                .setPeerIds(ids)
                .setMessage(text)
                .execute();
    }
}
