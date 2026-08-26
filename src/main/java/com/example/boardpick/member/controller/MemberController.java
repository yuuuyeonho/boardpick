package com.example.boardpick.member.controller;

import com.example.boardpick.member.dto.LoginRequest;
import com.example.boardpick.member.dto.MemberResponse;
import com.example.boardpick.member.dto.SignupRequest;
import com.example.boardpick.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "회원가입, 세션 로그인 및 회원 정보 API")
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping
    @Operation(summary = "회원가입", description = "회원을 생성하고 해당 회원의 개인 게임 리스트를 함께 생성합니다.")
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.signup(request));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 ID와 비밀번호로 인증하고 HTTP 세션을 생성합니다.")
    public MemberResponse login(@Valid @RequestBody LoginRequest request,
                                @Parameter(hidden = true) HttpServletRequest servletRequest,
                                @Parameter(hidden = true) HttpServletResponse servletResponse) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.loginId(), request.password()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, servletRequest, servletResponse);
        return memberService.getByLoginId(authentication.getName());
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 세션으로 로그인한 회원의 정보를 조회합니다.")
    public MemberResponse me(@Parameter(hidden = true) Principal principal) {
        return memberService.getByLoginId(principal.getName());
    }

    @DeleteMapping("/login")
    @Operation(summary = "로그아웃", description = "현재 HTTP 세션을 무효화하고 인증 정보를 제거합니다.")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
