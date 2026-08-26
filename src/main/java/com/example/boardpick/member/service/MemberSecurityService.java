package com.example.boardpick.member.service;

import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.domain.MemberRole;
import com.example.boardpick.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberSecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("회원을 찾을 수 없습니다."));
        MemberRole role = "admin".equals(member.getLoginId()) ? MemberRole.ADMIN : MemberRole.MEMBER;
        return new org.springframework.security.core.userdetails.User(member.getLoginId(), member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
    }
}
