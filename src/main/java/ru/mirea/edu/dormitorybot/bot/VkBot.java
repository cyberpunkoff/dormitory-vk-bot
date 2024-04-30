package ru.mirea.edu.dormitorybot.bot;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.methods.VkBotsMethods;
import api.longpoll.bots.model.events.Update;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.mirea.edu.dormitorybot.configuration.ApplicationConfig;
import ru.mirea.edu.dormitorybot.configuration.MinioProperties;
import ru.mirea.edu.dormitorybot.handlers.EchoHandler;
import ru.mirea.edu.dormitorybot.handlers.KeyboardDemo;
import ru.mirea.edu.dormitorybot.handlers.StateMachineHandler;

import java.util.List;

@Component
@Slf4j
public class VkBot extends LongPollBot implements AutoCloseable {
    private final ApplicationConfig applicationConfig;
    private final EchoHandler handler;
    private final KeyboardDemo keyboardDemo;
    private final StateMachineHandler stateMachineHandler;

    public VkBot(ApplicationConfig applicationConfig, @Lazy EchoHandler handler, @Lazy KeyboardDemo keyboardDemo, @Lazy StateMachineHandler stateMachineHandler) {
        this.applicationConfig = applicationConfig;
        this.handler = handler;
        this.keyboardDemo = keyboardDemo;
        this.stateMachineHandler = stateMachineHandler;
    }

    @Override
    public void handle(List<Update> updates) {
        updates.forEach(update -> {
            log.info("Got new update {}", update);
            try {
                stateMachineHandler.handle(update);
            } catch (VkApiException e) {
                throw new RuntimeException(e);
            }
        });
        // TODO: add update handling there
    }

    @Override
    public String getAccessToken() {
        return applicationConfig.getAccessToken();
    }


    // TODO: вобще не уверен насчет этого. правильно ли так делать?
    @Bean
    public VkBotsMethods vk() {
        return vk;
    }

    @Override
    public void startPolling() throws VkApiException {
        log.info("Starting vk bot...");
        super.startPolling();
    }

    @Override
    public void close() throws Exception {
        log.info("Shutting down vk bot...");
        this.stopPolling();
    }
}
