package Project.TiengAnhMoiNgay.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Builder
@Getter
@Setter
public class ReadingOptionCreate {
    private String optionText;
    private boolean isTrue;
}
