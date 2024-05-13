package ru.mirea.edu.dormitorybot.dao.entity;

import jakarta.persistence.*;
import lombok.*;
import ru.mirea.edu.dormitorybot.dto.EmployeeDto;

@Entity
@Table(name = "employee")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeEntity {
    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "email", nullable = false)
    private String email;

    public EmployeeDto toDto() {
        return new EmployeeDto(employeeName, description, phone, email);
    }
}
