package com.knucse.diy.api.reservation._diyManager;

import com.knucse.diy.api.reservation.dto.ReservationCancelDto;
import com.knucse.diy.api.reservation.dto.ReservationCreateDto;
import com.knucse.diy.api.reservation.dto.ReservationReadDto;
import com.knucse.diy.api.reservation.dto.ReservationStatusUpdateDto;
import com.knucse.diy.common.util.api.ApiResponseUtil;
import com.knucse.diy.common.util.api.ApiSuccessResult;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.service.reservation.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Tag(name = "예약 관리(동연의장)", description = "동연의장용 예약 API")
public class ReservationDiyManagerController {
    //예약 승인, 거절 API
    //예약 생성, 삭제 API
    private final ReservationService reservationService;

    @PatchMapping("/reservation/treatment")
    @Operation(summary = "예약 승인 또는 거부", description = "동연의장은 대기중인 예약을 승인 또는 거부할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 상태 변경 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> updateReservationStatus(
            @RequestBody ReservationStatusUpdateDto requestBody
    ) {
        ReservationReadDto responseBody = reservationService.updateReservationStatus(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @PatchMapping("/reservation/cancel")
    @Operation(summary = "예약 거절", description = "동연의장은 대기중인 예약을 거절할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 거절 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> cancelReservation(
            @RequestBody ReservationCancelDto requestBody
    ) {
        ReservationReadDto responseBody = reservationService.cancelReservation(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

}
