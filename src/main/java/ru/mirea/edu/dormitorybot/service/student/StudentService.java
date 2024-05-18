package ru.mirea.edu.dormitorybot.service.student;

import java.util.List;

public interface StudentService {
    void addStudent(Integer id);

    boolean isAdmin(Integer id);

    boolean isSuperadmin(Integer id);

    void makeStudentAdmin(Integer id);

    List<Integer> getStudents();

    void deleteAdmin(Integer newAdminId);
}
