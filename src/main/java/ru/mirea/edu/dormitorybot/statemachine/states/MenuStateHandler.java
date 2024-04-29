package ru.mirea.edu.dormitorybot.statemachine.states;

import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.basic.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mirea.edu.dormitorybot.exceptions.NoSuchEventException;
import ru.mirea.edu.dormitorybot.service.ScheduleImageService;
import ru.mirea.edu.dormitorybot.service.VkBotService;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import static ru.mirea.edu.dormitorybot.statemachine.Event.*;
import ru.mirea.edu.dormitorybot.statemachine.State;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuStateHandler extends StateHandler {
    private final VkBotService vkBotService;
    private final ScheduleImageService scheduleService;
    private final StateMachinePersister<State, Event, Integer> persister;
    private Keyboard keyboard;

    @PostConstruct
    void registerHandler() {
        State.MAIN_MENU.setStateHandler(this);
        keyboard = vkBotService.createKeyboard(
                List.of(GET_SCHEDULE.toString(), GET_EMPLOYEE.toString(), UPDATE_SCHEDULE.toString())
        );
    }

    @Override
    @SneakyThrows
    public void handle(StateMachine<State, Event> stateMachine, Message message) {
        try {
            Event event = Event.getEvent(message.getText());
            switch (event) {
                case GET_SCHEDULE:
//                    sendSchedule(message.getFromId());
                    break;
                case GET_EMPLOYEE:
                case UPDATE_SCHEDULE:
                    stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).subscribe();
                    break;
            }
        } catch (NoSuchEventException e) {
            vkBotService.sendTextMessageWithKeyboard(message.getPeerId(),
                    "Используйте клавиатуру!",
                    keyboard);
        }
    }

    @Override
    public void sendMessage(Integer id) {
        vkBotService.sendTextMessageWithKeyboard(id, "Добро пожаловать в общежитие!", keyboard);
    }

    public void sendSchedule(Integer id) {
        InputStream scheduleInputStream = scheduleService.getSchedule();
        vkBotService.sendTextWithImage(id, "Расписание", scheduleInputStream);
    }
}
