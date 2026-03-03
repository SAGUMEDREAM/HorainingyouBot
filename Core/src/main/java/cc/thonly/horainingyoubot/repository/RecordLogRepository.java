package cc.thonly.horainingyoubot.repository;

import cc.thonly.horainingyoubot.data.db.RecordLog;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordLogRepository extends JpaRepository<RecordLog, Long> {
    List<RecordLog> findAllByType(String type);


    List<RecordLog> findTopNByTypeOrderByTimeDesc(String type, Limit limit);
}
