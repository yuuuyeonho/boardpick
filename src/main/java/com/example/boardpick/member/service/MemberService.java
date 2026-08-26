package com.example.boardpick.member.service;

import com.example.boardpick.list.domain.GameList;
import com.example.boardpick.list.repository.GameListRepository;
import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.dto.SignupRequest;
import com.example.boardpick.member.dto.MemberResponse;
import com.example.boardpick.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final GameListRepository gameListRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse signup(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }
        Member member = memberRepository.save(new Member(request.loginId(),
                passwordEncoder.encode(request.password()), request.nickname()));
        gameListRepository.save(new GameList(member, member.getNickname() + "의 보드게임", false));
        return MemberResponse.from(member);
    }

    public MemberResponse getByLoginId(String loginId) {
        return MemberResponse.from(getEntityByLoginId(loginId));
    }

    public Member getEntityByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."));
    }
}
