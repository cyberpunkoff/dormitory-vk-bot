package ru.mirea.edu.dormitorybot.dao.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {
    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    @Column(name = "description", nullable = false)
    private String description;

    @Override
    public String toString() {
        return String.format("%s\n---\n%s", employeeName, description);
    }
}
