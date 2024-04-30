package ru.mirea.edu.dormitorybot.handlers;

import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.Update;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import static org.springframework.statemachine.StateMachineEventResult.ResultType.ACCEPTED;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mirea.edu.dormitorybot.service.VkBotService;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import ru.mirea.edu.dormitorybot.statemachine.State;

@Component
@Slf4j
@RequiredArgsConstructor
public class StateMachineHandler implements UpdateHandler {
    private final StateMachineFactory<State, Event> stateMachineFactory;
    private final StateMachinePersister<State, Event, Integer> persister;
    private final VkBotService vkBotService;

    @Override
    public void handle(Update update) throws VkApiException {
        if (update.getType() != Update.Type.MESSAGE_NEW) {
            return;
        }

        Message message = ((MessageNew) update.getObject()).getMessage();

        StateMachine<State, Event> stateMachine = stateMachineFactory.getStateMachine();
        try {
            persister.restore(stateMachine, message.getFromId());
            stateMachine.getExtendedState().getVariables().put("userId", message.getFromId());
        } catch (Exception ex) {
            log.info("Unable to restore state machine for user {}. Creating new state machine.",
                    message.getFromId());
            stateMachine = stateMachineFactory.getStateMachine();
        }


        stateMachine.getExtendedState().getVariables().put("message", message);

        try {
            Event event = Event.getEvent(message);
            log.info("Sending event {} to state machine", event);
            var result = stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(event).build()))
                    .collectList().block().getFirst().getResultType();
            if (result != ACCEPTED) {
                vkBotService.sendTextMessage(message.getFromId(), "Vi dodik?");
            }
            persister.persist(stateMachine, message.getFromId());
        } catch (Exception e) {
            log.error("Exception during execution {}", e.toString());
        }
    }
}
