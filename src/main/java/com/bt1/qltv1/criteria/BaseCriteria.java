package com.bt1.qltv1.criteria;

import com.bt1.qltv1.entity.BaseEntity_;
import com.bt1.qltv1.exception.BadRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.jhipster.service.Criteria;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class BaseCriteria extends Throwable implements Serializable, Criteria {
    private String fromTime;
    private String toTime;
    private BaseCriteria(BaseCriteria other){
        this.fromTime = other.fromTime;
        this.toTime = other.toTime;
    }
    @Override
    public Criteria copy() {
        return new BaseCriteria(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseCriteria that)) return false;
        return Objects.equals(fromTime, that.fromTime) && Objects.equals(toTime, that.toTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromTime, toTime);
    }

    public LocalDateTime getFormatFromTime() {
        LocalDateTime from;
        try {
            from = fromString(this.fromTime);
        }catch (DateTimeParseException ex){
            throw new BadRequest("Please enter right format of date ddMMyyyy HHmmss",
                    "from-date.invalid");
        }
        return from;
    }

    public LocalDateTime getFormatToTime() {
        LocalDateTime to;
        LocalDateTime from;
        try {
            from = getFormatFromTime();
            to = fromString(this.toTime);
            if(from.isAfter(to)){
                throw new BadRequest("The from time must is smaller than to time",
                        "time.invalid");
            }

        }catch (DateTimeParseException ex){
            throw new BadRequest("Please enter right format of date ddMMyyyy HHmmss",
                    "to-date.invalid");
        }
        return to;
    }

    private LocalDateTime fromString(String dateTime) throws DateTimeParseException {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("ddMMyyyy HHmmss"));
    }
}
