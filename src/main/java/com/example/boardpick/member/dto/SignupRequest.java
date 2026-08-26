package com.example.boardpick.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank @Size(min = 3, max = 20) String loginId,
        @NotBlank @Size(max = 30) String nickname,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
