package cc.thonly.horainingyoubot.service;

import cc.thonly.horainingyoubot.data.db.RecordLog;
import cc.thonly.horainingyoubot.repository.RecordLogRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class RecordLogServiceImpl {
    private static final Gson GSON = new Gson();
    @Autowired
    RecordLogRepository repository;

    public synchronized List<RecordLog> getByType(String type) {
        return this.repository.findAllByType(type);
    }

    public synchronized List<RecordLog> getLatest(String type, int limit) {
        return repository.findTopNByTypeOrderByTimeDesc(type, Limit.of(limit));
    }

    public synchronized <T> List<T> getInfoJsonObject(String type, Class<T> tClass) {
        return this.getByType(type).stream()
                .map(recordLog -> {
                    try {
                        return GSON.fromJson(recordLog.getInfo(), tClass);
                    } catch (Exception e) {
                        log.warn("JSON parse failed, type={}, content={}", type, recordLog.getInfo(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public synchronized void writeRecordLog(String type, String info) {
        RecordLog recordLog = new RecordLog();
        recordLog.setType(type);
        recordLog.setTime(LocalDateTime.now());
        recordLog.setInfo(info);
        this.repository.save(recordLog);
    }
}
