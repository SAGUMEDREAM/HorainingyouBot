package cc.thonly.intelligence_agencies.repository;

import cc.thonly.intelligence_agencies.data.CollectCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectCacheRepository extends JpaRepository<CollectCache, Long> {
    CollectCache findTopByOrderByTimeAsc();
}
