package ru.mirea.edu.dormitorybot.service.minio.employee;

import java.io.InputStream;

public interface EmployeeImageService {
    InputStream getEmployeePicture(Long id);
}
