package Project.TiengAnhMoiNgay.AWS.services;

import Project.TiengAnhMoiNgay.AWS.config.DatabaseContextHolder;
import Project.TiengAnhMoiNgay.AWS.entities.TestEntity;
import Project.TiengAnhMoiNgay.AWS.repositories.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class DatabaseTestService {
    
    @Autowired
    private TestRepository testRepository;
    
    @Autowired
    @Qualifier("writerJdbcTemplate")
    private JdbcTemplate writerJdbcTemplate;
    
    @Autowired
    @Qualifier("readerJdbcTemplate")
    private JdbcTemplate readerJdbcTemplate;
    
    @Autowired
    @Qualifier("drReaderJdbcTemplate")
    private JdbcTemplate drReaderJdbcTemplate;
    
    // Write Operation - Writer Endpoint
    @Transactional
    public TestEntity performWriteOperation(String data) {
        DatabaseContextHolder.setDatabaseType("writer");
        long startTime = System.currentTimeMillis();
        
        try {
            TestEntity entity = new TestEntity(data, "WRITE", "writer-singapore");
            TestEntity saved = testRepository.save(entity);
            
            long duration = System.currentTimeMillis() - startTime;
            saved.setResponseTimeMs(duration);
            
            return testRepository.save(saved);
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }
    
    // Read Operation - Local Reader
    @Transactional(readOnly = true)
    public TestEntity performLocalReadOperation(Long id) {
        DatabaseContextHolder.setDatabaseType("reader");
        long startTime = System.currentTimeMillis();
        
        try {
            TestEntity entity = testRepository.findById(id).orElse(null);
            if (entity != null) {
                long duration = System.currentTimeMillis() - startTime;
                // Log performance without modifying entity
                logPerformance("READ", "reader-singapore", duration);
            }
            return entity;
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }
    
    // Read Operation - Cross-Region Reader
    @Transactional(readOnly = true)
    public TestEntity performCrossRegionReadOperation(Long id) {
        DatabaseContextHolder.setDatabaseType("dr-reader");
        long startTime = System.currentTimeMillis();
        
        try {
            TestEntity entity = testRepository.findById(id).orElse(null);
            if (entity != null) {
                long duration = System.currentTimeMillis() - startTime;
                logPerformance("READ", "reader-nvirginia", duration);
            }
            return entity;
        } finally {
            DatabaseContextHolder.clearDatabaseType();
        }
    }
    
    // Test Connection Health
    public Map<String, Object> testConnectionHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // Test Writer
        try {
            Integer result = writerJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("writer_status", "healthy");
            health.put("writer_response_time", measureConnectionTime("writer"));
        } catch (Exception e) {
            health.put("writer_status", "unhealthy");
            health.put("writer_error", e.getMessage());
        }
        
        // Test Local Reader
        try {
            Integer result = readerJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("local_reader_status", "healthy");
            health.put("local_reader_response_time", measureConnectionTime("reader"));
        } catch (Exception e) {
            health.put("local_reader_status", "unhealthy");
            health.put("local_reader_error", e.getMessage());
        }
        
        // Test Cross-Region Reader
        try {
            Integer result = drReaderJdbcTemplate.queryForObject("SELECT 1", Integer.class);
            health.put("cross_region_reader_status", "healthy");
            health.put("cross_region_reader_response_time", measureConnectionTime("dr-reader"));
        } catch (Exception e) {
            health.put("cross_region_reader_status", "unhealthy");
            health.put("cross_region_reader_error", e.getMessage());
        }
        
        return health;
    }
    
    // Measure Replication Lag
    public long measureReplicationLag() {
        try {
            // Insert record with timestamp on writer
            String insertSql = "INSERT INTO replication_test (test_timestamp) VALUES (NOW(6))";
            writerJdbcTemplate.update(insertSql);
            
            // Wait a moment
            Thread.sleep(100);
            
            // Read from cross-region reader
            String selectSql = "SELECT TIMESTAMPDIFF(MICROSECOND, test_timestamp, NOW(6)) as lag_microseconds FROM replication_test ORDER BY id DESC LIMIT 1";
            Long lagMicroseconds = drReaderJdbcTemplate.queryForObject(selectSql, Long.class);
            
            return lagMicroseconds / 1000; // Convert to milliseconds
        } catch (Exception e) {
            return -1;
        }
    }
    
    private long measureConnectionTime(String endpoint) {
        long startTime = System.currentTimeMillis();
        try {
            switch (endpoint) {
                case "writer":
                    writerJdbcTemplate.queryForObject("SELECT 1", Integer.class);
                    break;
                case "reader":
                    readerJdbcTemplate.queryForObject("SELECT 1", Integer.class);
                    break;
                case "dr-reader":
                    drReaderJdbcTemplate.queryForObject("SELECT 1", Integer.class);
                    break;
            }
        } catch (Exception e) {
            // Handle exception
        }
        return System.currentTimeMillis() - startTime;
    }
    
    private void logPerformance(String operation, String endpoint, long duration) {
        System.out.println(String.format("[PERFORMANCE] %s on %s took %d ms", 
                                       operation, endpoint, duration));
    }
}
