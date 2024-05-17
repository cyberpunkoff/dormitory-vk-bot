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
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import ru.mirea.edu.dormitorybot.service.*;
import ru.mirea.edu.dormitorybot.service.employee.EmployeeInfoService;
import ru.mirea.edu.dormitorybot.service.student.StudentService;

import java.util.EnumSet;

@Configuration
@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<State, Event> {
    private final ScheduleService scheduleService;
    private final HelperService helperService;
    private final StudentService studentService;
    private final VkBotService vkBotService;
    private final EmployeeInfoService employeeInfoService;
    private final AdminService adminService;
    private final RulesService rulesService;
    private final MenuService menuService;
    private final NewsletterService newsLetterService;

    @Override
    public void configure(StateMachineConfigurationConfigurer<State, Event> config) throws Exception {
        config.withConfiguration().autoStartup(true).listener(listener());
    }

    private StateMachineListener<State, Event> listener() {

        return new StateMachineListenerAdapter<State, Event>() {
            @Override
            public void eventNotAccepted(org.springframework.messaging.Message<Event> event) {
                log.error("Not accepted event: {}", event);
            }
        };
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
                .source(State.MAIN_MENU).target(State.MAIN_MENU)
                .event(Event.REGISTER)
                .action(registerStudentAction())
                .and()
                .withExternal()
                .source(State.ADMIN_MENU).target(State.MAIN_MENU)
                .event(Event.BACK)
                .action(sendMenuAction())
                .and()
                .withExternal()
                .source(State.MAIN_MENU).target(State.ADMIN_MENU)
                .event(Event.ADMIN_PANEL)
                .guard(isAdminGuard())
                .action(sendAdminMenuAction())
                .and()
                .withExternal()
                .source(State.CHOOSE_EMPLOYEE).target(State.CHOOSE_EMPLOYEE)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(sendEmployeeInfoAction())
                .and()
                .withExternal()
                .source(State.ADMIN_MENU).target(State.ADD_ADMIN)
                .event(Event.ADD_ADMIN)
                .action(sendAddDeleteRequestAdminInfoAction())
                .and()
                .withExternal()
                .source(State.ADD_ADMIN).target(State.ADD_ADMIN)
                .event(Event.BACK)
                .action(sendAdminMenuAction())
                .and()
                .withExternal()
                .source(State.DELETE_ADMIN).target(State.ADMIN_MENU)
                .event(Event.BACK)
                .action(sendAdminMenuAction())
                .and()
                .withExternal()
                .source(State.ADMIN_MENU).target(State.DELETE_ADMIN)
                .event(Event.DELETE_ADMIN)
                .action(sendAddDeleteRequestAdminInfoAction())
                .and()
                .withExternal()
                .source(State.ADD_ADMIN).target(State.MAIN_MENU)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(addAdminAction())
                .and()
                .withExternal()
                .source(State.DELETE_ADMIN).target(State.MAIN_MENU)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(deleteAdminAction())
                .and()
                .withExternal()
                .source(State.MAIN_MENU).target(State.MAIN_MENU)
                .event(Event.GET_RULES)
                .action(sendRulesAction())
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
                .source(State.ADMIN_MENU).target(State.SEND_SCHEDULE_PHOTO)
                .event(Event.UPDATE_SCHEDULE)
                .action(askForPhotoAction())
                .and()

                .withExternal()
                .source(State.ADMIN_MENU).target(State.WAITING_NEWSLETTER)
                .event(Event.CREATE_NEWSLETTER)
                .action(createNewsletterAction())
                .and()
                .withExternal()
                .source(State.WAITING_NEWSLETTER).target(State.ADMIN_MENU)
                .event(Event.BACK)
                .action(exitWaitingState())
                .and()
                .withExternal()
                .source(State.WAITING_NEWSLETTER).target(State.APPROVE_NEWSLETTER)
                .event(Event.UNKNOWN_TEXT_RECEIVED)
                .action(handleNewsletter())
                .and()
                .withExternal()
                .source(State.APPROVE_NEWSLETTER).target(State.ADMIN_MENU)
                .event(Event.CANCEL)
                .action(cancelNewsletterSending())
                .and()
                .withExternal()
                .source(State.APPROVE_NEWSLETTER).target(State.ADMIN_MENU)
                .event(Event.APPROVE)
                .action(sendNewsletter())
                .and()
                .withExternal()
                .source(State.APPROVE_NEWSLETTER).target(State.WAITING_NEWSLETTER)
                .event(Event.EDIT_NEWSLETTER)
                .action(createNewsletterAction())
                .and()

                .withExternal()
                .source(State.ADMIN_MENU).target(State.EDIT_EMPLOYEE)
                .event(Event.EDIT_EMPLOYEE_INFO)
                .action(editEmployeeInfoAction())
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
    public Guard<State, Event> isAdminGuard() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            return studentService.isAdmin(userId);
        };
    }

    @Bean
    Action<State, Event> sendMenuAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            menuService.sendMenu(userId);
        };
    }

    @Bean
    Action<State, Event> sendAddDeleteRequestAdminInfoAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            adminService.sendRequest(userId);
        };
    }

    @Bean
    Action<State, Event> addAdminAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            adminService.addAdmin(message);
        };
    }

    @Bean
    Action<State, Event> deleteAdminAction() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            adminService.deleteAdmin(message);
        };
    }

    @Bean
    Action<State, Event> sendAdminMenuAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            menuService.sendAdminMenu(userId);
        };
    }

    @Bean
    Action<State, Event> createNewsletterAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            menuService.sendBackFromStateMenu(userId);
        };
    }

    @Bean
    Action<State, Event> exitWaitingState() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            menuService.sendAdminMenu(userId);
        };
    }

    @Bean
    Action<State, Event> handleNewsletter() {
        return context -> {
            Message message = context.getExtendedState().get("message", Message.class);
            Integer userId = message.getFromId();
            log.info("newsletter: {}", message);
            menuService.sendNewsletterMenu(userId);
        };
    }

    @Bean
    Action<State, Event> cancelNewsletterSending() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            menuService.sendAdminMenu(userId);
            vkBotService.sendTextMessage(userId, "Отмена рассылки сообщения");
        };
    }

    @Bean
    Action<State, Event> sendNewsletter() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            Message newsletter = context.getExtendedState().get("newsletter", Message.class);
            newsLetterService.sendNewsLetterForEveryone(newsletter);
            vkBotService.sendTextMessage(userId, "Сообщение отправлено всем пользователям");
            menuService.sendAdminMenu(userId);
        };
    }

    @Bean
    Action<State, Event> editEmployeeInfoAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            employeeInfoService.sendEmployees(userId);
        };
    }

    @Bean
    Action<State, Event> registerStudentAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            studentService.addStudent(userId);
            menuService.sendMenu(userId);
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

    @Bean
    Action<State, Event> sendRulesAction() {
        return context -> {
            Integer userId = context.getExtendedState().get("message", Message.class).getFromId();
            rulesService.sendRules(userId);
        };
    }
}
