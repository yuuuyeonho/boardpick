package com.example.boardpick.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/oauth2")
@Tag(name = "OAuth", description = "소셜 로그인 진입 API")
public class OAuthController {

    @GetMapping("/google")
    @Operation(
            summary = "Google 로그인 시작",
            description = "Google OAuth 인증 화면으로 리디렉션합니다. " +
                    "fetch보다 브라우저 페이지 이동으로 호출해야 합니다."
    )
    public ResponseEntity<Void> googleLogin() {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/oauth2/authorization/google"))
                .build();
    }
}
