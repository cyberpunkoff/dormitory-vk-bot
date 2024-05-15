package ru.mirea.edu.dormitorybot.service.student;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.edu.dormitorybot.dao.entity.Role;
import ru.mirea.edu.dormitorybot.dao.entity.StudentEntity;
import ru.mirea.edu.dormitorybot.dao.repository.StudentRepository;
import ru.mirea.edu.dormitorybot.exceptions.StudentNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JpaStudentService implements StudentService {
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public void addStudent(Integer id) {
        if (studentRepository.findById(id).isEmpty()) {
            StudentEntity student = new StudentEntity(id, Role.ROLE_USER);
            studentRepository.save(student);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAdmin(Integer id) {
        StudentEntity student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        return student.getRole().equals(Role.ROLE_ADMIN) || student.getRole().equals(Role.ROLE_SUPERADMIN);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSuperadmin(Integer id) {
        StudentEntity student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        return student.getRole().equals(Role.ROLE_SUPERADMIN);
    }

    @Override
    @Transactional
    public void makeStudentAdmin(Integer id) {
        StudentEntity student = studentRepository.findById(id).orElseThrow(StudentNotFoundException::new);
        student.setRole(Role.ROLE_ADMIN);
    }

    //Для отправки сообщений, мб стоит не ставить конкретную роль
    @Override
    @Transactional(readOnly = true)
    public List<Integer> getStudents() {
        return studentRepository.findAllByRole(Role.ROLE_USER).stream().map(StudentEntity::getStudentId).toList();
    }
}
