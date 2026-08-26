package com.example.boardpick.member.domain;

import com.example.boardpick.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "social_account", uniqueConstraints =
        @UniqueConstraint(name = "uk_social_provider_user", columnNames = {"provider", "provider_user_id"}))
@Getter
@NoArgsConstructor
public class SocialAccount extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    private String email;

    public SocialAccount(Member member, OAuthProvider provider, String providerUserId, String email) {
        this.member = member;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.email = email;
    }
}
