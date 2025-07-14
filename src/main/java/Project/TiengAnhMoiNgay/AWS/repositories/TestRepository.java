package Project.TiengAnhMoiNgay.AWS.repositories;

import Project.TiengAnhMoiNgay.AWS.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    
    @Query("SELECT t FROM TestEntity t WHERE t.operationType = ?1 ORDER BY t.createdAt DESC")
    List<TestEntity> findByOperationType(String operationType);
    
    @Query("SELECT t FROM TestEntity t WHERE t.endpointUsed = ?1 ORDER BY t.createdAt DESC")
    List<TestEntity> findByEndpointUsed(String endpointUsed);
}
