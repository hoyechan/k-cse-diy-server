package com.knucse.diy.api.reservation._diyManager;

import com.knucse.diy.api.reservation.dto.*;
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

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "예약 관리(동연의장)", description = "동연의장용 예약 API")
public class ReservationDiyManagerController {
    //예약 승인, 거절 API
    //예약 생성, 삭제 API
    private final ReservationService reservationService;

    @PatchMapping("/reservation/treatment")
    @Operation(summary = "예약 승인", description = "동연의장은 대기중인 예약을 승인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 승인 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> approveReservation(
            @RequestBody ReservationStatusUpdateDto requestBody
    ) {
        ReservationReadDto responseBody = reservationService.updateReservationStatus(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @PatchMapping("/reservation/treatment-list")
    @Operation(summary = "예약 리스트 승인", description = "동연의장은 대기중인 예약들을 승인할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약들 승인 성공"),
            @ApiResponse(responseCode = "404", description = "예약들을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> approveReservationList(
            @RequestBody List<Long> requestBody
    ) {
        List<ReservationReadDto> responseBody = reservationService.updateReservationListStatus(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }
//
//    @PatchMapping("/reservation/cancel")
//    @Operation(summary = "예약 거절", description = "동연의장은 대기중인 예약을 거절할 수 있습니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "예약 거절 성공"),
//            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
//    })
//    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> cancelReservation(
//            @RequestBody ReservationCancelDto requestBody
//    ) {
//        ReservationReadDto responseBody = reservationService.cancelReservation(requestBody);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
//    }

    @PatchMapping("/reservation/cancel")
    @Operation(summary = "예약 리스트 거절", description = "동연의장은 대기중인 예약들을 거절할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약들 거절 성공"),
            @ApiResponse(responseCode = "404", description = "예약들을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> cancelReservationList(
            @RequestBody ReservationCancelDto requestBody
    ) {
        List<ReservationReadDto> responseBody = reservationService.cancelReservationList(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    //인증 번호 새로 만들어주는 api
    @PatchMapping("/reservation/authcode")
    @Operation(summary = "인증 번호 수정", description = "동연의장은 인증 번호를 바꿀 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 거절 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<ReservationReadDto>> updateAuthCode(
            @RequestBody ReservationAuthCodeUpdateDto requestBody
    ) {
        ReservationReadDto responseBody = reservationService.updateAuthCode(requestBody);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    //인증 번호 없어도 삭제 가능한 api
    @DeleteMapping("/reservation/delete/{reservationId}")
    @Operation(summary = "예약 삭제", description = "관리자는 기존 예약 정보를 삭제할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음 (code: RESERVATION_NOT_FOUND)"),
    })
    public ResponseEntity<ApiSuccessResult<Void>> deleteReservation(
            @PathVariable("reservationId") Long reservationId
    ) {
        reservationService.deleteReservation(reservationId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponseUtil.success(HttpStatus.NO_CONTENT));
    }

    @GetMapping("/reservation/studentName")
    @Operation(summary = "특정 학생 이름으로 예약 조회", description = "관리자는 학생의 이름으로 예약을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 조회 성공"),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음 (code: STUDENT_NOT_FOUND)")
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> findReservationByStudentName(
            @RequestParam("studentName") String studentName
    ) {
        List<ReservationReadDto> responseBody = reservationService.findReservationByStudentName(studentName);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @GetMapping("/reservation/studentNumber")
    @Operation(summary = "특정 학생 학번으로 예약 조회", description = "관리자는 학생의 학번으로 예약을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 조회 성공"),
            @ApiResponse(responseCode = "404", description = "학생을 찾을 수 없음 (code: STUDENT_NOT_FOUND)")
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> findReservationByStudentNumber(
            @RequestParam("studentNumber") String studentNumber
    ) {
        List<ReservationReadDto> responseBody = reservationService.findReservationByStudentNumber(studentNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @GetMapping("/reservation/status")
    @Operation(summary = "예약 상태로 예약 조회", description = "관리자는 예약 상태로 예약을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 조회 성공"),
    })
    public ResponseEntity<ApiSuccessResult<List<ReservationReadDto>>> findReservationByStatus(
            @RequestParam("status") ReservationStatus status
    ) {
        List<ReservationReadDto> responseBody = reservationService.findReservationByStatus(status);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, responseBody));
    }

    @GetMapping("/validate-token")
    @Operation(summary = "토큰 검증", description = "클라이언트 딴에서 토큰 검증을 하기 위한 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 검증 성공"),
            @ApiResponse(responseCode = "401", description = "토큰 검증 실패")
    })
    public ResponseEntity<ApiSuccessResult<Void>> validateToken(
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponseUtil.success(HttpStatus.OK, null));
    }
}
