package ru.mirea.edu.dormitorybot.statemachine.states;

import api.longpoll.bots.model.objects.basic.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import ru.mirea.edu.dormitorybot.statemachine.Event;
import ru.mirea.edu.dormitorybot.statemachine.State;

import java.util.HashMap;
import java.util.Map;

public abstract class StateHandler {
    private static final Map<State, StateHandler> stateToHandler = new HashMap<>();

    @Autowired
    private void setupHandlers(@Lazy ApproveScheduleUpdateState approveScheduleUpdateState,
                       @Lazy ReceivePhotoState receivePhotoState) {
        stateToHandler.put(State.SEND_SCHEDULE_PHOTO, receivePhotoState);
        stateToHandler.put(State.APPROVE_SCHEDULE_UPDATE, approveScheduleUpdateState);
    }

    public static StateHandler getHandler(State state) {
        return stateToHandler.get(state);
    }

    public abstract void handle(StateMachine<State, Event> stateMachine, Message message);
    public abstract void sendMessage(Integer id);

}
