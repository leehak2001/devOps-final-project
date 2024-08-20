package ac.il.hit.final_project;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DateRangeRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public DateRangeRequest(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
