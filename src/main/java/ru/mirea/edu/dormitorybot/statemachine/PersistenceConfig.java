package ru.mirea.edu.dormitorybot.statemachine;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

@Configuration
public class PersistenceConfig {
    @Bean
    public StateMachinePersist<State, Event, Integer> inMemoryPersist() {
        return new InMemoryPersist();
    }

    @Bean
    public StateMachinePersister<State, Event, Integer> persister(StateMachinePersist<State, Event, Integer> defaultPersist) {
        return new DefaultStateMachinePersister<>(defaultPersist);
    }
}
