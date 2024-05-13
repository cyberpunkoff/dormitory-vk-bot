package ru.mirea.edu.dormitorybot.service.minio.schedule;

import ru.mirea.edu.dormitorybot.exceptions.NoScheduleForCurrentMonthException;

import java.io.InputStream;

public interface ScheduleImageService {
    InputStream getSchedule() throws NoScheduleForCurrentMonthException;

    boolean updateSchedule(InputStream schedulePictureStream);
}
