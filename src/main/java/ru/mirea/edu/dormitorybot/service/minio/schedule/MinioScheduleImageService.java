package ru.mirea.edu.dormitorybot.service.minio.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mirea.edu.dormitorybot.exceptions.NoScheduleForCurrentMonthException;
import ru.mirea.edu.dormitorybot.service.minio.MinioService;

import java.io.InputStream;
import java.time.Month;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MinioScheduleImageService implements ScheduleImageService {
    // @Value("${app.bucket}") Зачем тут эти штуки? У нас же это будет супер статичная тема, думаю можно просто статиками
    private final static String SCHEDULE_BUCKET = "schedule";
    private final static String SCHEDULE_PICTURE = "schedule-picture";
    private final MinioService minioService;

    @Override
    public InputStream getSchedule() throws NoScheduleForCurrentMonthException {
        if (isScheduleActualForCurrentMonth()) {
            return minioService.getFile(SCHEDULE_BUCKET, SCHEDULE_PICTURE);
        } else {
            throw new NoScheduleForCurrentMonthException();
        }
    }

    @Override
    public boolean updateSchedule(InputStream schedulePictureStream) {
        minioService.putFile(SCHEDULE_BUCKET, SCHEDULE_PICTURE, schedulePictureStream);
        return true;
    }

    private boolean isScheduleActualForCurrentMonth() {
        var scheduleFileStats = minioService.getFileStats(SCHEDULE_BUCKET, SCHEDULE_PICTURE);

        Month scheduleLastUpdatedMonth = scheduleFileStats.lastModified().getMonth();
        Month currentMonth = OffsetDateTime.now().getMonth();

        return currentMonth.equals(scheduleLastUpdatedMonth);
    }
}
