package Project.TiengAnhMoiNgay.AWS.config;

import org.springframework.stereotype.Component;

@Component
public class DatabaseContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    
    public static void setDatabaseType(String databaseType) {
        contextHolder.set(databaseType);
    }
    
    public static String getDatabaseType() {
        return contextHolder.get();
    }
    
    public static void clearDatabaseType() {
        contextHolder.remove();
    }
}
