package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.additional.Keyboard;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.entity.Employee;
import ru.mirea.edu.dormitorybot.statemachine.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeInfoService {
    private final VkBotService vkBotService;
    private final Map<String, Employee> employees = new HashMap<>();

    // не круто, что храним вместе сущности и логику
    @PostConstruct
    private void createEmployees() {
        employees.put("MarIO", new Employee("Mario", "Funny computer game hero"));
        employees.put("DEAD", new Employee("Dedushka", "old man"));
    }

    public List<String> getEmployeeNames() {
        return new ArrayList<>(employees.keySet());
    }

    public void sendEmployees(Integer id) {
        List<String> labels = getEmployeeNames();
        labels.add(Event.BACK.toString());
        Keyboard keyboard = VkBotService.createKeyboard(labels);

        vkBotService.sendTextMessageWithKeyboard(id, "Список сотрудников", keyboard);
    }

    public void sendInfoAboutEmployee(Integer id, String employeeName) {
        if (employees.containsKey(employeeName)) {
            vkBotService.sendTextMessage(id, employees.get(employeeName).toString());
        } else {
            vkBotService.sendTextMessage(id, "Такого сотрудника нет в общежитии");
        }
    }
}
