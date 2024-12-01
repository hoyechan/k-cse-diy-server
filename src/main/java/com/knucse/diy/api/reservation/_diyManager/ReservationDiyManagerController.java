package com.knucse.diy.api.reservation._diyManager;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DIY_MANAGER') and isAuthenticated()")
@Tag(name = "예약 관리(동연의장)", description = "동연의장용 예약 API")
public class ReservationDiyManagerController {

}
