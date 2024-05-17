package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.additional.Keyboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.student.StudentService;
import ru.mirea.edu.dormitorybot.statemachine.Event;

import static ru.mirea.edu.dormitorybot.statemachine.Event.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final StudentService studentService;
    public final static Keyboard MENU_KEYBOARD = VkBotService.createKeyboard(
            List.of(GET_SCHEDULE.toString(), GET_RULES.toString(), GET_EMPLOYEE.toString())
    );
    public final static Keyboard MENU_KEYBOARD_FOR_ADMIN = VkBotService.createKeyboard(
            List.of(GET_SCHEDULE.toString(),
                    GET_RULES.toString(),
                    GET_EMPLOYEE.toString(),
                    ADMIN_PANEL.toString())
    );
    public final static Keyboard ADMIN_MENU_KEYBOARD = VkBotService.createKeyboard(
            List.of(UPDATE_SCHEDULE.toString(),
                    CREATE_NEWSLETTER.toString(),
                    EDIT_EMPLOYEE_INFO.toString(),
                    ADD_ADMIN.toString(),
                    DELETE_ADMIN.toString(),
                    BACK.toString())
    );
    public final static Keyboard MENU_BACK_FROM_CURRENT_STATE = VkBotService.createKeyboard(
            List.of(BACK.toString())
    );

    public final static Keyboard MENU_NEWSLETTER = VkBotService.createKeyboard(
            List.of(APPROVE.toString(),
                    EDIT_NEWSLETTER.toString(),
                    CANCEL.toString())
    );

    private final VkBotService vkBotService;

    public void sendMenu(Integer id) {
        if (studentService.isAdmin(id)) {
            vkBotService.sendTextMessageWithKeyboard(
                    id,
                    "Добро пожаловать во второе общежитие!",
                    MENU_KEYBOARD_FOR_ADMIN
            );
        } else {
            vkBotService.sendTextMessageWithKeyboard(id, "Добро пожаловать во второе общежитие!", MENU_KEYBOARD);
        }
    }

    public void sendAdminMenu(Integer id) {
        vkBotService.sendTextMessageWithKeyboard(
                id,
                "Здравствуйте, уважаемый администратор! Чем займетесь сегодня?",
                ADMIN_MENU_KEYBOARD
        );
    }

    public void sendBackFromStateMenu(Integer id) {
        vkBotService.sendTextMessageWithKeyboard(
                id,
                "Наберите и отправьте сообщение, которое хотите отослать всем пользователям бота",
                MENU_BACK_FROM_CURRENT_STATE
        );
    }

    public void sendNewsletterMenu(Integer id) {
        vkBotService.sendTextMessageWithKeyboard(
                id,
                "Подтвердите ваше действие",
                MENU_NEWSLETTER
        );
    }
}
