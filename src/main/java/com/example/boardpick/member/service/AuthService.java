package com.example.boardpick.member.service;

import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.list.repository.GameListRepository;
import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.domain.OAuthProvider;
import com.example.boardpick.member.domain.SocialAccount;
import com.example.boardpick.member.dto.GoogleLoginRequest;
import com.example.boardpick.member.dto.LoginResponse;
import com.example.boardpick.member.oauth.GoogleOAuthUserInfo;
import com.example.boardpick.member.repository.MemberRepository;
import com.example.boardpick.member.repository.SocialAccountRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final GoogleIdTokenVerifier verifier;
    private final SocialAccountRepository socialAccountRepository;
    private final MemberRepository memberRepository;
    private final GameListRepository gameListRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request) {
        GoogleIdToken idToken = verifyGoogleIdToken(request.idToken());
        GoogleIdToken.Payload payload = idToken.getPayload();

        String providerUserId = payload.getSubject();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new BadCredentialsException("Google 사용자 식별자(sub)가 없습니다.");
        }

        OAuthProvider provider = OAuthProvider.GOOGLE;
        GoogleOAuthUserInfo userInfo = new GoogleOAuthUserInfo(payload);

        Member member = socialAccountRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(SocialAccount::getMember)
                .orElseGet(() -> createMember(provider, providerUserId, userInfo.email(), userInfo.nickname()));

        return LoginResponse.from(member);
    }

    private Member createMember(OAuthProvider provider, String providerUserId, String email, String nickname) {
        String loginId = createLoginId(provider, providerUserId);
        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        Member member = memberRepository.save(new Member(loginId, password, nickname));

        socialAccountRepository.save(new SocialAccount(member, provider, providerUserId, email));
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

    private GoogleIdToken verifyGoogleIdToken(String idToken) {
        try {
            GoogleIdToken token =
                    verifier.verify(idToken);

            if(token == null) {
                throw new BadCredentialsException("유효하지 않은  ID Token");
            }

            return token;

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
