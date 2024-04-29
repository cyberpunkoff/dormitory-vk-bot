package ru.mirea.edu.dormitorybot.statemachine.states;

import api.longpoll.bots.model.objects.basic.Message;
import api.longpoll.bots.model.objects.media.Photo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mirea.edu.dormitorybot.service.VkBotService;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import ru.mirea.edu.dormitorybot.statemachine.State;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceivePhotoState extends StateHandler {
    private final VkBotService vkBotService;
    private final StateMachinePersister<State, Event, Integer> persister;

    @PostConstruct
    void registerHandler() {
        State.SEND_SCHEDULE_PHOTO.setStateHandler(this);
    }

    public void handle(StateMachine<State, Event> stateMachine, Message message) {
        if (checkMessageHasPhotoAttachment(message)) {
            String photoUrl = getPhotoUrlFromMessage(message);
            stateMachine.getExtendedState().getVariables().put("SCHEDULE_URL", photoUrl);
            stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(Event.PHOTO_RECEIVED).build())).subscribe();
        }
    }

    @Override
    public void sendMessage(Integer id) {
        vkBotService.sendTextMessage(id, "Пришлите фотографию расписания");
    }

    private boolean checkMessageHasPhotoAttachment(Message message) {
        return message.getAttachments().size() == 1 && message.getAttachments().getFirst().getPhoto() != null;
    }

    private String getPhotoUrlFromMessage(Message message) {
        Photo photo = message.getAttachments().getFirst().getPhoto();
        return photo.getPhotoSizes().getLast().getSrc();
    }
}
