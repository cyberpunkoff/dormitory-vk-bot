package ru.mirea.edu.dormitorybot.service.employee;

import ru.mirea.edu.dormitorybot.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {
    EmployeeDto getEmployeeData(String name);
    List<String> getEmployeesNames();
}
