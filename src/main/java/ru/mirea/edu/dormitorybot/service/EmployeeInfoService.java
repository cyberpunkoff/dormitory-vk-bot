package ru.mirea.edu.dormitorybot.service;

import api.longpoll.bots.model.objects.additional.Keyboard;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.dao.entity.EmployeeEntity;
import ru.mirea.edu.dormitorybot.dao.repository.EmployeeRepository;
import ru.mirea.edu.dormitorybot.statemachine.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmployeeInfoService {
    private final VkBotService vkBotService;
    private final EmployeeRepository employeeRepository;
    //    private final Map<String, Employee> employees = new HashMap<>();
//
//    // не круто, что храним вместе сущности и логику
//    @PostConstruct
//    private void createEmployees() {
//        employees.put("MarIO", new Employee("Mario", "Funny computer game hero"));
//        employees.put("DEAD", new Employee("Dedushka", "old man"));
//    }
    public List<String> getEmployeeNames() {
        return employeeRepository.findAll().stream().map(EmployeeEntity::getEmployeeName).toList();
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
        Optional<EmployeeEntity> employee = employeeRepository.findEmployeeEntityByEmployeeName(employeeName);
        if (employee.isPresent()) {
            vkBotService.sendTextMessage(id, employee.get().toString());
        } else {
            vkBotService.sendTextMessage(id, "Такого сотрудника нет в общежитии");
        }
    }
}
