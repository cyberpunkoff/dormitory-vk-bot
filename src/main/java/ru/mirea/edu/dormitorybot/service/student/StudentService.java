package ru.mirea.edu.dormitorybot.service.student;

import java.util.List;

public interface StudentService {
    void addStudent(Long id);

    boolean isAdmin(Long id);

    boolean isSuperadmin(Long id);

    void makeStudentAdmin(Long id);

    List<Long> getStudents();
}
