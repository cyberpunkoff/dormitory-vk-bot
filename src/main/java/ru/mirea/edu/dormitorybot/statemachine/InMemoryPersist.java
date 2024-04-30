package ru.mirea.edu.dormitorybot.statemachine;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;

import java.util.HashMap;

public class InMemoryPersist implements StateMachinePersist<State, Event, Integer> {
    private final HashMap<Integer, StateMachineContext<State, Event>> storage = new HashMap<>();

    @Override
    public void write(StateMachineContext<State, Event> context, Integer contextObj) throws Exception {
        storage.put(contextObj, context);
    }

    @Override
    public StateMachineContext<State, Event> read(Integer contextObj) throws Exception {
        return storage.get(contextObj);
    }
}
