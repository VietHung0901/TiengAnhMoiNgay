package Project.TiengAnhMoiNgay.AWS.controllers;

import Project.TiengAnhMoiNgay.AWS.entities.TestEntity;
import Project.TiengAnhMoiNgay.AWS.services.DatabaseTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/aurora-test")
public class DatabaseTestController {
    
    @Autowired
    private DatabaseTestService databaseTestService;
    
    // Test Write Operation
    @PostMapping("/write")
    public ResponseEntity<?> testWriteOperation(@RequestBody Map<String, String> request) {
        long startTime = System.currentTimeMillis();
        
        try {
            String data = request.get("data");
            TestEntity result = databaseTestService.performWriteOperation(data);
            long totalDuration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("operation", "WRITE");
            response.put("endpoint", "writer-singapore");
            response.put("total_duration_ms", totalDuration);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "operation", "WRITE",
                "error", e.getMessage()
            ));
        }
    }
    
    // Test Local Read Operation
    @GetMapping("/read/local/{id}")
    public ResponseEntity<?> testLocalReadOperation(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TestEntity result = databaseTestService.performLocalReadOperation(id);
            long totalDuration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("operation", "READ");
            response.put("endpoint", "reader-singapore");
            response.put("total_duration_ms", totalDuration);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "operation", "READ_LOCAL",
                "error", e.getMessage()
            ));
        }
    }
    
    // Test Cross-Region Read Operation
    @GetMapping("/read/cross-region/{id}")
    public ResponseEntity<?> testCrossRegionReadOperation(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        
        try {
            TestEntity result = databaseTestService.performCrossRegionReadOperation(id);
            long totalDuration = System.currentTimeMillis() - startTime;
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("operation", "READ");
            response.put("endpoint", "reader-nvirginia");
            response.put("total_duration_ms", totalDuration);
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "operation", "READ_CROSS_REGION",
                "error", e.getMessage()
            ));
        }
    }
    
    // Test Connection Health
    @GetMapping("/health")
    public ResponseEntity<?> testConnectionHealth() {
        try {
            Map<String, Object> health = databaseTestService.testConnectionHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }
    
    // Test Replication Lag
    @GetMapping("/replication-lag")
    public ResponseEntity<?> testReplicationLag() {
        try {
            long lag = databaseTestService.measureReplicationLag();
            
            Map<String, Object> response = new HashMap<>();
            response.put("replication_lag_ms", lag);
            response.put("status", lag < 1000 ? "good" : "high_lag");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
    }
    
    // Performance Comparison Test
    @GetMapping("/performance-comparison/{id}")
    public ResponseEntity<?> performanceComparison(@PathVariable Long id) {
        Map<String, Object> results = new HashMap<>();
        
        // Test Local Read
        long localStart = System.currentTimeMillis();
        try {
            TestEntity localResult = databaseTestService.performLocalReadOperation(id);
            long localDuration = System.currentTimeMillis() - localStart;
            results.put("local_read", Map.of(
                "duration_ms", localDuration,
                "status", "success",
                "endpoint", "singapore-reader"
            ));
        } catch (Exception e) {
            results.put("local_read", Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
        
        // Test Cross-Region Read
        long crossRegionStart = System.currentTimeMillis();
        try {
            TestEntity crossRegionResult = databaseTestService.performCrossRegionReadOperation(id);
            long crossRegionDuration = System.currentTimeMillis() - crossRegionStart;
            results.put("cross_region_read", Map.of(
                "duration_ms", crossRegionDuration,
                "status", "success",
                "endpoint", "nvirginia-reader"
            ));
        } catch (Exception e) {
            results.put("cross_region_read", Map.of(
                "status", "error",
                "error", e.getMessage()
            ));
        }
        
        return ResponseEntity.ok(results);
    }
}
