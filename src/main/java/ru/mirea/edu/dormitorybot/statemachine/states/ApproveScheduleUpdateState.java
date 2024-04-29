package ru.mirea.edu.dormitorybot.statemachine.states;

import api.longpoll.bots.model.objects.additional.Keyboard;
import api.longpoll.bots.model.objects.basic.Message;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mirea.edu.dormitorybot.service.ScheduleImageService;
import ru.mirea.edu.dormitorybot.service.VkBotService;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import ru.mirea.edu.dormitorybot.statemachine.State;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApproveScheduleUpdateState extends StateHandler {
    private final ScheduleImageService scheduleService;
    private final VkBotService vkBotService;
    private final StateMachinePersister<State, Event, Integer> persister;

    @PostConstruct
    void registerHandler() {
        State.APPROVE_SCHEDULE_UPDATE.setStateHandler(this);
    }

    @Override
    public void sendMessage(Integer id) {
        Keyboard keyboard = vkBotService.createKeyboard(List.of(Event.APPROVE.toString(), Event.CANCEL.toString()));
        vkBotService.sendTextMessageWithKeyboard(id, "Подтвердите обновление расписания:", keyboard);
    }

    @Override
    @SneakyThrows
    public void handle(StateMachine<State, Event> stateMachine, Message message) {
        Event event = Event.getEvent(message.getText());

        switch (event) {
            case APPROVE:
                String photoUrl = (String) stateMachine.getExtendedState().getVariables().get("SCHEDULE_URL");

                try (InputStream photoStream = URI.create(photoUrl).toURL().openStream()) {
                    scheduleService.updateSchedule(photoStream);
                } catch (IOException e) {
                    log.error("Error updating photo! {}", e.getMessage());
                }

                vkBotService.sendTextMessage(message.getPeerId(), "Расписание обновлено!");
                break;
            case CANCEL:
                vkBotService.sendTextMessage(message.getPeerId(), "Отмена!");
                stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build())).subscribe();
                persister.persist(stateMachine, message.getFromId());
                stateMachine.getState().getId().getStateHandler().sendMessage(message.getId());
                break;
            default:
                vkBotService.sendTextMessage(message.getPeerId(), "Используйте клавиатуру!");
        }
    }
}