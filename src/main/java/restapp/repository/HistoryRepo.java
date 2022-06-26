package restapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import restapp.entity.HistoryEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface HistoryRepo extends CrudRepository<HistoryEntity, Long> {
    @Query("SELECT p FROM HistoryEntity p WHERE (p.product.id = ?1) AND (p.date BETWEEN ?2 AND ?3)")
    List<HistoryEntity> findAllByProductIdAndInterval(UUID id, Date start, Date finish);
}
