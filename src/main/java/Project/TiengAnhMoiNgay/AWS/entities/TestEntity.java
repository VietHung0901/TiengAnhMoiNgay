package Project.TiengAnhMoiNgay.AWS.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_test")
public class TestEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "test_data")
    private String data;
    
    @Column(name = "operation_type")
    private String operationType;
    
    @Column(name = "endpoint_used")
    private String endpointUsed;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "response_time_ms")
    private Long responseTimeMs;
    
    // Constructors, getters, setters
    public TestEntity() {}
    
    public TestEntity(String data, String operationType, String endpointUsed) {
        this.data = data;
        this.operationType = operationType;
        this.endpointUsed = endpointUsed;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    
    public String getEndpointUsed() { return endpointUsed; }
    public void setEndpointUsed(String endpointUsed) { this.endpointUsed = endpointUsed; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
}
