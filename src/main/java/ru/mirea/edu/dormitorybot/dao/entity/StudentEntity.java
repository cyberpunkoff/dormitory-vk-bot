package ru.mirea.edu.dormitorybot.dao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StudentEntity {
    @Id
    @Column(name = "student_id", unique = true, nullable = false)
    private Integer studentId;

    @Enumerated(EnumType.STRING)
    private Role role;
}
