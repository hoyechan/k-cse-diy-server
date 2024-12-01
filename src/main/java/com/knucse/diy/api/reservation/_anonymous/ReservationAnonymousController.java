package com.knucse.diy.api.reservation._anonymous;

import com.knucse.diy.api.reservation.dto.ReservationDeleteDto;
import com.knucse.diy.api.reservation.dto.ReservationUpdateDto;
import com.knucse.diy.common.util.api.ApiResponseUtil;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import com.knucse.diy.api.reservation.dto.ReservationCreateDto;
import com.knucse.diy.api.reservation.dto.ReservationReadDto;
import com.knucse.diy.common.util.api.ApiSuccessResult;
import com.knucse.diy.domain.service.reservation.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Tag(name = "예약(사용자)", description = "사용자용 예약 API")
public class ReservationAnonymousController {
    private final ReservationService reservationService;

    @PostMapping("/reservation/create")
    @Operation(summary = "예약 생성", description = "사용자는 예약 가능한 시간대에 예약을 할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음 (code: STUDENT_NOT_FOUND)"),
            @ApiResponse(responseCode = "409", description = "시간이 중복되는 예약이 존재함 (code: RESERVATION_DUPLICATED)"),
            @ApiResponse(responseCode = "400", description = "인증 코드는 4자리 숫자여야함 (code: AUTHENTICATION_CODE_MUST_BE_4_DIGITS)")
    })
    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> createReservation(
            @Valid @RequestBody ReservationCreateDto requestBody
    ) {
        ReservationReadDto responseBody = reservationService.createReservation(requestBody);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, responseBody));
    }

    @GetMapping("/reservation/{studentName}/{studentNumber}")
    @Operation(summary = "특정 학생의 예약 조회", description = "사용자는 이름과 학번으로 예약을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 조회 성공"),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음 (code: STUDENT_NOT_FOUND)")
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> findReservationByStudent(
            @PathVariable String studentName,
            @PathVariable String studentNumber
    ) {
        List<ReservationReadDto> responseBody = reservationService.findReservationsByStudent(studentName, studentNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @GetMapping("/reservation/{year}/{month}")
    @Operation(summary = "예약 월별로 조회", description = "사용자는 월별로 예약 정보를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 조회 성공"),
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> findReservationByYearMonth(
            @PathVariable int year,
            @PathVariable int month
    ) {
        YearMonth yearMonth = YearMonth.of(year,month);
        List<ReservationReadDto> responseBody = reservationService.findReservationsByMonth(yearMonth);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @PostMapping("/reservation/update")
    @Operation(summary = "예약 수정", description = "사용자는 기존 예약 정보를 수정할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 수정 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
            @ApiResponse(responseCode = "400", description = "인증 코드는 4자리 숫자여야 함 (code: AUTHENTICATION_CODE_MUST_BE_4_DIGITS)")
    })
    public ResponseEntity<ApiSuccessResult<Void>> updateReservation(
            @Valid @RequestBody ReservationUpdateDto requestBody
    ) {
        reservationService.updateReservation(requestBody);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponseUtil.success(HttpStatus.NO_CONTENT));
    }

    @PostMapping("/reservation/delete")
    @Operation(summary = "예약 삭제", description = "사용자는 기존 예약 정보를 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
            @ApiResponse(responseCode = "400", description = "인증 코드는 4자리 숫자여야 함 (code: AUTHENTICATION_CODE_MUST_BE_4_DIGITS)")
    })
    public ResponseEntity<ApiSuccessResult<Void>> deleteReservation(
            @Valid @RequestBody ReservationDeleteDto requestBody
    ) {
        reservationService.deleteReservation(requestBody);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponseUtil.success(HttpStatus.NO_CONTENT));
    }
}
