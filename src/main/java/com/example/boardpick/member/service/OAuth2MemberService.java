package com.example.boardpick.member.service;

import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.list.repository.GameListRepository;
import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.domain.OAuthProvider;
import com.example.boardpick.member.domain.SocialAccount;
import com.example.boardpick.member.oauth.OAuthUserInfo;
import com.example.boardpick.member.oauth.OAuthUserInfoFactory;
import com.example.boardpick.member.repository.MemberRepository;
import com.example.boardpick.member.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String LOGIN_ID_ATTRIBUTE = "boardpickLoginId";

    private final MemberRepository memberRepository;
    private final SocialAccountRepository socialAccountRepository;
    private final GameListRepository gameListRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(userRequest);
        OAuthProvider provider = resolveProvider(userRequest.getClientRegistration().getRegistrationId());
        OAuthUserInfo userInfo = OAuthUserInfoFactory.create(provider, oauthUser.getAttributes());

        if (userInfo.providerUserId() == null || userInfo.providerUserId().isBlank()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("missing_provider_user_id"),
                    "OAuth 사용자 식별자가 없습니다.");
        }

        Member member = socialAccountRepository
                .findByProviderAndProviderUserId(provider, userInfo.providerUserId())
                .map(SocialAccount::getMember)
                .orElseGet(() -> createMember(provider, userInfo));

        Map<String, Object> attributes = new HashMap<>(oauthUser.getAttributes());
        attributes.put(LOGIN_ID_ATTRIBUTE, member.getLoginId());

        return new DefaultOAuth2User(
                java.util.List.of(new SimpleGrantedAuthority("ROLE_MEMBER")),
                attributes,
                LOGIN_ID_ATTRIBUTE
        );
    }

    private Member createMember(OAuthProvider provider, OAuthUserInfo userInfo) {
        String loginId = createLoginId(provider, userInfo.providerUserId());
        String unavailablePassword = passwordEncoder.encode(UUID.randomUUID().toString());
        Member member = memberRepository.save(new Member(loginId, unavailablePassword, userInfo.nickname()));

        socialAccountRepository.save(new SocialAccount(
                member, provider, userInfo.providerUserId(), userInfo.email()));
        gameListRepository.save(new GameList(member, member.getNickname() + "의 보드게임", false));
        return member;
    }

    private String createLoginId(OAuthProvider provider, String providerUserId) {
        String base = provider.name().toLowerCase(Locale.ROOT) + "_" + providerUserId;
        if (!memberRepository.existsByLoginId(base)) {
            return base;
        }
        return base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private OAuthProvider resolveProvider(String registrationId) {
        try {
            return OAuthProvider.valueOf(registrationId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unsupported_provider"),
                    "지원하지 않는 OAuth 제공자입니다.", exception);
        }
    }
}
