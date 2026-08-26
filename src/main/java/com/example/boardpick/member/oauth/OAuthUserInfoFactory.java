package com.example.boardpick.member.oauth;

import com.example.boardpick.member.domain.OAuthProvider;

import java.util.Map;

public final class OAuthUserInfoFactory {

    private OAuthUserInfoFactory() {
    }

    public static OAuthUserInfo create(OAuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuthUserInfo(attributes);
            case KAKAO -> throw new IllegalArgumentException("아직 지원하지 않는 OAuth 제공자입니다: " + provider);
        };
    }
}
