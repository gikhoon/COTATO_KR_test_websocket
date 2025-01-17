package org.cotato.csquiz.api.session.dto;

import static org.cotato.csquiz.domain.attendance.enums.DeadLine.DEFAULT_ATTENDANCE_DEADLINE;
import static org.cotato.csquiz.domain.attendance.enums.DeadLine.DEFAULT_LATE_DEADLINE;

import java.time.LocalDate;
import java.util.Objects;
import org.cotato.csquiz.api.attendance.dto.AttendanceDeadLineDto;
import org.cotato.csquiz.domain.attendance.embedded.Location;
import org.cotato.csquiz.domain.generation.enums.CSEducation;
import org.cotato.csquiz.domain.generation.enums.DevTalk;
import org.cotato.csquiz.domain.generation.enums.ItIssue;
import org.cotato.csquiz.domain.generation.enums.Networking;
import jakarta.validation.constraints.NotNull;

public record UpdateSessionRequest(
        @NotNull
        Long sessionId,
        String title,
        String description,
        @NotNull
        LocalDate sessionDate,
        String placeName,
        Location location,
        AttendanceDeadLineDto attendTime,
        @NotNull
        ItIssue itIssue,
        @NotNull
        Networking networking,
        @NotNull
        CSEducation csEducation,
        @NotNull
        DevTalk devTalk
) {
    public UpdateSessionRequest {
        if (Objects.isNull(attendTime)) {
            attendTime = new AttendanceDeadLineDto(DEFAULT_ATTENDANCE_DEADLINE.getTime(),
                    DEFAULT_LATE_DEADLINE.getTime());
        }
    }
}
