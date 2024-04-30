package ru.mirea.edu.dormitorybot.statemachine;

import api.longpoll.bots.model.objects.basic.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.mirea.edu.dormitorybot.service.EmployeeInfoService;
import ru.mirea.edu.dormitorybot.service.HelperService;
import ru.mirea.edu.dormitorybot.service.ScheduleService;

import java.util.EnumSet;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<State, Event> {
    private final ScheduleService scheduleService;
    private final HelperService helperService;
    private final EmployeeInfoService employeeInfoService;

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration().autoStartup(true);
    }

    @Override
    public void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states.withStates()
                .initial(State.MAIN_MENU)
                .states(EnumSet.allOf(State.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withExternal()
                .source(State.MAIN_MENU).target(State.CHOOSE_EMPLOYEE)
                .event(Event.GET_EMPLOYEE)
                .action(sendEmployeesAction())
                .and()
                .withExternal()
                .source(State.CHOOSE_EMPLOYEE).target(State.CHOOSE_EMPLOYEE)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(sendEmployeeInfoAction())
                .and()
                .withExternal()
                .source(State.MAIN_MENU).target(State.MAIN_MENU)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(sendMenuAction())
                .and()
                .withExternal()
                .source(State.CHOOSE_EMPLOYEE).target(State.MAIN_MENU)
                .event(Event.BACK)
                .action(sendMenuAction())
                .and()
                .withExternal()
                .source(State.MAIN_MENU).target(State.SEND_SCHEDULE_PHOTO)
                .event(Event.UPDATE_SCHEDULE)
                .action(askForPhotoAction())
                .and()
                .withExternal()
                .source(State.MAIN_MENU).target(State.MAIN_MENU)
                .event(Event.GET_SCHEDULE)
                .action(sendScheduleAction())
                .and()
                .withExternal()
                .source(State.SEND_SCHEDULE_PHOTO).target(State.APPROVE_SCHEDULE_UPDATE)
                .event(Event.PHOTO_RECEIVED)
                .action(photoReceivedAction())
                .and()
                .withExternal()
                .source(State.SEND_SCHEDULE_PHOTO).target(State.MAIN_MENU)
                .event(Event.CANCEL)
                .action(cancelAction())
                .and()
                .withExternal()
                .source(State.SEND_SCHEDULE_PHOTO).target(State.SEND_SCHEDULE_PHOTO)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .and()
                .withExternal()
                .source(State.APPROVE_SCHEDULE_UPDATE).target(State.MAIN_MENU)
                .event(Event.APPROVE)
                .action(updateScheduleAction())
                .and()
                .withExternal()
                .source(State.APPROVE_SCHEDULE_UPDATE).target(State.MAIN_MENU)
                .event(Event.CANCEL)
                .action(cancelAction());
    }

    @Bean
    Action<State, Event> sendMenuAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            helperService.sendMenu(userId);
        };
    }

    @Bean
    Action<State, Event> sendEmployeesAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            employeeInfoService.sendEmployees(userId);
        };
    }

    @Bean
    Action<State, Event> sendEmployeeInfoAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            employeeInfoService.sendInfoAboutEmployee(userId, message.getText());
        };
    }

    @Bean
    Action<State, Event> sendScheduleAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            log.info("Sending schedule to {}", userId);
            scheduleService.sendSchedule(userId);
        };
    }

    @Bean
    Action<State, Event> askForPhotoAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            scheduleService.askForPhoto(userId);
        };
    }

    @Bean
    Action<State, Event> photoReceivedAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            String photoUrl = ScheduleService.getPhotoUrlFromMessage(message);
            context.getExtendedState().getVariables().put("photoUrl", photoUrl);
            helperService.askForConfirmation(userId);
        };
    }

    @Bean
    Action<State, Event> updateScheduleAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            String photoUrl = context.getExtendedState().get("photoUrl", String.class);
            scheduleService.updateSchedule(userId, photoUrl);
        };
    }

    @Bean
    Action<State, Event> cancelAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            helperService.sendActionCanceledMessage(userId);
        };
    }
}