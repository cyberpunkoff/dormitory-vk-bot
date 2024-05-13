package ru.mirea.edu.dormitorybot.service.employee;

import api.longpoll.bots.model.objects.additional.Keyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.dto.EmployeeDto;
import ru.mirea.edu.dormitorybot.exceptions.EmployeeNotFoundException;
import ru.mirea.edu.dormitorybot.service.VkBotService;
import ru.mirea.edu.dormitorybot.statemachine.Event;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeInfoService {
    private final VkBotService vkBotService;
    private final EmployeeService employeeService;

    public List<String> getEmployeeNames() {
        return employeeService.getEmployeesNames();
    }

    public void sendEmployees(Integer id) {
        List<String> labels = new ArrayList<>(getEmployeeNames());
        log.info(labels);
        log.info(Event.BACK.toString());
        labels.add(Event.BACK.toString());
        Keyboard keyboard = VkBotService.createKeyboard(labels);

        vkBotService.sendTextMessageWithKeyboard(id, "Список сотрудников", keyboard);
    }

    public void sendInfoAboutEmployee(Integer id, String employeeName) {
        try {
            EmployeeDto employeeInfo = employeeService.getEmployeeData(employeeName);
            vkBotService.sendTextMessage(id, makeInfoMessage(employeeInfo));
        } catch (EmployeeNotFoundException e) {
            vkBotService.sendTextMessage(id, "Такого сотрудника нет в общежитии");
        }
    }

    private String makeInfoMessage(EmployeeDto employeeDto) {
        return "ФИО: %s\nДополнительная информация: %s\nНомер телефона: %s\nПочта:%s"
                .formatted(employeeDto.name(), employeeDto.description(), employeeDto.phone(), employeeDto.email());
    }
}
