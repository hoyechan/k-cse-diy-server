package com.knucse.diy.authentication.filter;


import com.knucse.diy.common.util.jwt.JWTUtil;
import com.knucse.diy.domain.model.admin.Admin;
import com.knucse.diy.domain.model.admin.AdminDetails;
import com.knucse.diy.domain.model.student.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // jwt를 검증하는 filter
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request의 header에서 "Authorization" key를 가진 값을 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if(authorization == null || !authorization.startsWith("Bearer ")) {

            System.out.println("token null");
            //지금 filter에서 request와 response를 다음 filter로 전달
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)) {

            System.out.println("token is expired");
            filterChain.doFilter(request, response);

            return;
        }

        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);


        Admin admin = Admin.builder()
                .username(username)
                .role(Role.ROLE_DIY_MANAGER)
                .password("temp")
                .build();


        AdminDetails adminDetails = new AdminDetails(admin);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
