package ru.mirea.edu.dormitorybot.service.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.edu.dormitorybot.dao.entity.EmployeeEntity;
import ru.mirea.edu.dormitorybot.dao.repository.EmployeeRepository;
import ru.mirea.edu.dormitorybot.dto.EmployeeDto;
import ru.mirea.edu.dormitorybot.exceptions.EmployeeNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaEmployeeService implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeData(String name) {
        EmployeeEntity employee = employeeRepository.findEmployeeEntityByEmployeeName(name)
                .orElseThrow(EmployeeNotFoundException::new);
        return employee.toDto();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getEmployeesNames() {
        return employeeRepository.findAll().stream().map(EmployeeEntity::getEmployeeName).toList();
    }
}
