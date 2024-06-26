package ru.mirea.edu.dormitorybot.statemachine;

import api.longpoll.bots.model.objects.basic.Message;
import ru.mirea.edu.dormitorybot.exceptions.NoSuchEventException;
import ru.mirea.edu.dormitorybot.service.ScheduleService;

import java.util.HashMap;
import java.util.Map;

public enum Event {
    GET_SCHEDULE("Расписание"),
    UPDATE_SCHEDULE("Обновить расписание"),
    EDIT_EMPLOYEE_INFO("Обновить информацию о работнике"),
    PHOTO_RECEIVED,
    ADD_ADMIN("Добавить администратора"),
    DELETE_ADMIN("Удалить администратора"),
    APPROVE("Подтвердить"),
    CANCEL("Отмена"),
    UNKNOWN_TEXT_RECEIVED,
    ADMIN_PANEL("Админ-панель"),
    GET_EMPLOYEE("Сотрудники"),
    CREATE_NEWSLETTER("Создать рассылку"),
    BACK("Назад"),
    EDIT_NEWSLETTER("Редактировать"),
    GET_RULES("Правила общежития"),
    REGISTER("Начать");

    private final static Map<String, Event> messageToEvent = new HashMap<>();
    private String messageText;

    static {
        for (Event event: Event.values()) {
            if (event.messageText != null) {
                messageToEvent.put(event.messageText, event);
            }
        }
    }

    public static Event getEvent(Message message) {
        Event event = messageToEvent.getOrDefault(message.getText(), Event.UNKNOWN_TEXT_RECEIVED);

        if (ScheduleService.checkMessageHasPhotoAttachment(message)) {
            event = Event.PHOTO_RECEIVED;
        }

        return event;
    }

    Event() {
    }

    Event(String messageText) {
        this.messageText = messageText;
    }

    @Override
    public String toString() {
        return messageText;
    }
}
