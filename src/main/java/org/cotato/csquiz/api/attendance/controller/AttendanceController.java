package org.cotato.csquiz.api.attendance.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cotato.csquiz.api.attendance.dto.AttendanceRecordResponse;
import org.cotato.csquiz.api.attendance.dto.UpdateAttendanceRequest;
import org.cotato.csquiz.domain.attendance.service.AttendanceAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v2/api/attendances")
public class AttendanceController {

    private final AttendanceAdminService attendanceAdminService;

    @Operation(summary = "출석 정보 변경 API")
    @PatchMapping
    public ResponseEntity<Void> updateAttendance(@RequestBody @Valid UpdateAttendanceRequest request) {
        attendanceAdminService.updateAttendance(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 출결사항 기간 단위 조회 API")
    @GetMapping("/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecords(
            @RequestParam(name = "generationId") Long generationId,
            @RequestParam(name = "month", required = false) @Min(value = 1, message = "달은 1 이상이어야 합니다.") @Max(value = 12, message = "달은 12 이하이어야 합니다") Integer month
    ) {
        return ResponseEntity.ok().body(attendanceAdminService.findAttendanceRecords(generationId, month));
    }

    @Operation(summary = "회원 출결사항 출석 단위 조회 API")
    @GetMapping("/{attendance-id}/records")
    public ResponseEntity<List<AttendanceRecordResponse>> findAttendanceRecordsByAttendance(
            @PathVariable("attendance-id") Long attendanceId) {
        return ResponseEntity.ok().body(attendanceAdminService.findAttendanceRecordsByAttendance(attendanceId));
    }
}