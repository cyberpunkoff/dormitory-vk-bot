package ru.mirea.edu.dormitorybot.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mirea.edu.dormitorybot.dao.entity.Role;
import ru.mirea.edu.dormitorybot.dao.entity.StudentEntity;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findAllByRole(Role role);
}
