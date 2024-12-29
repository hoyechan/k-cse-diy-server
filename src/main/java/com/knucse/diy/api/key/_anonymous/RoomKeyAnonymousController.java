package com.knucse.diy.api.key._anonymous;

import com.knucse.diy.api.key.dto.KeyRentDto;
import com.knucse.diy.api.key.dto.KeyReturnDto;
import com.knucse.diy.api.reservation.dto.ReservationCreateDto;
import com.knucse.diy.api.reservation.dto.ReservationReadDto;
import com.knucse.diy.common.util.api.ApiResponseUtil;
import com.knucse.diy.common.util.api.ApiSuccessResult;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.service.key.RoomKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@Tag(name = "열쇠(사용자)", description = "사용자용 열쇠 API")
public class RoomKeyAnonymousController {

    private final RoomKeyService roomKeyService;

    @PostMapping("/roomkey/rent")
    @Operation(summary = "열쇠 대여", description = "사용자는 열쇠를 대여할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "열쇠 대여 성공"),
            @ApiResponse(responseCode = "400", description = "열쇠 대여 실패 (code: KEY_RENT_AUTHENTICATION_FAILED)")
    })
    public ResponseEntity<ApiSuccessResult<RoomKeyStatus>> rentRoomKey(
            @Valid @RequestBody KeyRentDto requestBody
    ) {

        RoomKeyStatus responseBody = roomKeyService.rentKey(requestBody);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, responseBody));

    }

    @PostMapping("/roomkey/return")
    @Operation(summary = "열쇠 반납", description = "사용자는 열쇠를 반납할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "열쇠 반납 성공"),
            @ApiResponse(responseCode = "400", description = "열쇠 반납 실패 (code: KEY_RETURN_AUTHENTICATION_FAILED)")
    })
    public ResponseEntity<ApiSuccessResult<RoomKeyStatus>> returnRoomKey(
            @Valid @RequestBody KeyReturnDto requestBody
    ) {

        RoomKeyStatus responseBody = roomKeyService.returnKey(requestBody);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, responseBody));

    }
}
