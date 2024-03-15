package ru.mirea.edu.dormitorybot.handlers;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.Update;

public interface UpdateHandler {
    void handle(Update update) throws VkApiException;
}
