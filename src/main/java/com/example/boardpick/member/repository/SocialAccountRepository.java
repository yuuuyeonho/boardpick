package com.example.boardpick.member.repository;

import com.example.boardpick.member.domain.OAuthProvider;
import com.example.boardpick.member.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
