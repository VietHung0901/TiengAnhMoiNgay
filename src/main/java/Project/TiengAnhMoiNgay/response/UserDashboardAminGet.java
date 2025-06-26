package Project.TiengAnhMoiNgay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class UserDashboardAminGet {
    private String name;
    private LocalDate createdAt;
}
