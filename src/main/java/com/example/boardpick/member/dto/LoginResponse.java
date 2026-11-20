package com.example.boardpick.member.dto;

import com.example.boardpick.member.domain.Member;

public record LoginResponse(
        Long memberId,
        String loginId,
        String nickname,
        String role
) {
    public static LoginResponse from(Member member) {
        return new LoginResponse(
                member.getId(),
                member.getLoginId(),
                member.getNickname(),
                member.getRole().toString()
        );
    }
}
