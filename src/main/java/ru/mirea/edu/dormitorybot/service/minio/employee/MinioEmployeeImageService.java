package ru.mirea.edu.dormitorybot.service.minio.employee;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.service.minio.MinioService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioEmployeeImageService implements EmployeeImageService {
    private static final String EMPLOYEE_BUCKET = "employees";

    private final MinioService minioService;

    @Override
    public InputStream getEmployeePicture(Long id) {
        return minioService.getFile(EMPLOYEE_BUCKET, id.toString() + ".jpg");
    }
}
