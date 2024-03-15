package ru.mirea.edu.dormitorybot.handlers;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.events.Update;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EchoHandler implements UpdateHandler {
    private final VkBotsMethods vk;

    @Override
    public void handle(Update update) throws VkApiException {
        if (!update.getType().equals(Update.Type.MESSAGE_NEW)) {
            return;
        }

        Message message = ((MessageNew) update.getObject()).getMessage();

        String userName = vk.users.get()
                .setUserIds(String.valueOf(message.getPeerId()))
                .setFields("first_name")
                .execute()
                .getResponse().getFirst().getFirstName();

        String response = userName + " сказал: " + message.getText();

        vk.messages.send()
                .setPeerId(message.getPeerId())
                .setMessage(response)
                .execute();
    }
}
