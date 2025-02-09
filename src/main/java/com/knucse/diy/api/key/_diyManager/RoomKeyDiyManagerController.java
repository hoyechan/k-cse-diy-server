package com.knucse.diy.api.key._diyManager;

import com.knucse.diy.api.key.dto.KeyHistoryReadDto;
import com.knucse.diy.api.key.dto.KeyReadDto;
import com.knucse.diy.api.key.dto.KeyRentDto;
import com.knucse.diy.common.util.api.ApiResponseUtil;
import com.knucse.diy.common.util.api.ApiSuccessResult;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.service.key.RoomKeyHistoryService;
import com.knucse.diy.domain.service.key.RoomKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "열쇠 관리(동연의장)", description = "동연의장용 열쇠 API")
public class RoomKeyDiyManagerController {

    private final RoomKeyHistoryService roomKeyHistoryService;
    private final RoomKeyService roomKeyService;

    @GetMapping("/roomkeyhistory")
    @Operation(summary = "열쇠 히스토리 조회", description = "관리자는 열쇠 히스토리를 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "열쇠 히스토리 조회 성공"),
    })
    public ResponseEntity<ApiSuccessResult<List<KeyHistoryReadDto>>> getRoomKeyHistory() {

        List<KeyHistoryReadDto> responseBody = roomKeyHistoryService.findAllRoomKeyHistory();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, responseBody));
    }

    @PatchMapping("/roomkey/return")
    @Operation(summary = "열쇠 반납 처리", description = "관리자는 강제로 열쇠를 반납상태로 바꿀 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "열쇠 대여 성공"),
    })
    public ResponseEntity<ApiSuccessResult<KeyReadDto>> returnRoomKey(
    ) {
        KeyReadDto responseBody = roomKeyService.updateRoomKey(null, RoomKeyStatus.KEEPING);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponseUtil.success(HttpStatus.CREATED, responseBody));
    }
}
