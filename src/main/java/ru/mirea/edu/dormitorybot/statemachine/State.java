package ru.mirea.edu.dormitorybot.statemachine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import ru.mirea.edu.dormitorybot.statemachine.states.ApproveScheduleUpdateState;
import ru.mirea.edu.dormitorybot.statemachine.states.StateHandler;

public enum State {
    MAIN_MENU,
    SEND_SCHEDULE_PHOTO,
    APPROVE_SCHEDULE_UPDATE,
    CHOOSE_EMPLOYEE,
    INPUT_NEWSLETTER_TEXT,
    APPROVE_NEWSLETTER;

    private StateHandler stateHandler;

    public void setStateHandler(StateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }

    public StateHandler getStateHandler() {
        return stateHandler;
    }
}
