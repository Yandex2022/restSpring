package restapp.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import restapp.entity.ProductEntity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ProductRepo extends CrudRepository <ProductEntity, UUID> {

    List<ProductEntity> findAllByParentId(UUID parentId);

    List<ProductEntity> findAllByDateBetween(Date start, Date finish);

    @Query("SELECT p FROM ProductEntity p WHERE (p.date BETWEEN ?1 AND ?2)")
    List<ProductEntity> findAllBetween(Date start, Date finish);
}
