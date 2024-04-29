package ru.mirea.edu.dormitorybot.service;

import ru.mirea.edu.dormitorybot.exceptions.NoScheduleForCurrentMonthException;

import java.io.InputStream;

public interface ScheduleService {
    InputStream getSchedule() throws NoScheduleForCurrentMonthException;

    boolean updateSchedule(InputStream schedulePictureStream);
}
