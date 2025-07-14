-- Create table for replication lag testing
CREATE TABLE IF NOT EXISTS replication_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_timestamp TIMESTAMP(6) DEFAULT CURRENT_TIMESTAMP(6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table for performance testing (if not exists via JPA)
CREATE TABLE IF NOT EXISTS performance_test (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    test_data TEXT,
    operation_type VARCHAR(50),
    endpoint_used VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    response_time_ms BIGINT
);
