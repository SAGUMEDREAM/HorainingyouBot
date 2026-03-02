package cc.thonly.horainingyoubot.repository;

import cc.thonly.horainingyoubot.data.db.JsonData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JsonDataRepository extends JpaRepository<JsonData, Long> {
    Long KEY_TH_SEARCHER = 0L;
    Long KEY_TH_GROUP = 1L;
}
