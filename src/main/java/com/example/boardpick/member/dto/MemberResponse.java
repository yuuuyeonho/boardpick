package com.example.boardpick.member.dto;

import com.example.boardpick.member.domain.Member;

public record MemberResponse(Long id, String loginId, String nickname) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getLoginId(), member.getNickname());
    }
}
