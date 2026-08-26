package com.example.boardpick.member.oauth;

import java.util.Map;

public record GoogleOAuthUserInfo(Map<String, Object> attributes) implements OAuthUserInfo {

    @Override
    public String providerUserId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String email() {
        return (String) attributes.get("email");
    }

    @Override
    public String nickname() {
        String name = (String) attributes.get("name");
        if (name != null && !name.isBlank()) {
            return name;
        }
        String email = email();
        if (email == null || email.isBlank()) {
            return "Boardpick 회원";
        }
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }
}
