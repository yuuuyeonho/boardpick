package com.example.boardpick.member.controller;

import com.example.boardpick.member.domain.Member;
import com.example.boardpick.member.dto.GoogleLoginRequest;
import com.example.boardpick.member.service.OAuth2MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OAuthControllerTest {
    final JwtDecoder decoder = mock(JwtDecoder.class);
    final OAuth2MemberService members = mock(OAuth2MemberService.class);
    final OAuthController controller = new OAuthController(decoder, members,
            new HttpSessionSecurityContextRepository(), "client");
    final MockHttpServletRequest request = new MockHttpServletRequest();
    final MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach void cleanup() { SecurityContextHolder.clearContext(); }

    Jwt token(String nonce) {
        return Jwt.withTokenValue("token").header("alg", "RS256")
                .subject("google-user").claim("nonce", nonce).build();
    }

    @Test void createsSessionRotatesIdAndConsumesNonce() {
        String nonce = controller.config(request).getBody().nonce();
        String oldId = request.getSession().getId();
        Jwt token = token(nonce);
        when(decoder.decode("token")).thenReturn(token);
        when(members.findOrCreate(token.getClaims())).thenReturn(new Member("google_user", "unused", "회원"));
        var result = controller.login(new GoogleLoginRequest("token"), request, response);
        assertEquals("google_user", result.loginId());
        assertNotEquals(oldId, request.getSession().getId());
        assertNotNull(request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"));
        assertEquals("google_user", SecurityContextHolder.getContext().getAuthentication().getName());
        assertThrows(ResponseStatusException.class,
                () -> controller.login(new GoogleLoginRequest("token"), request, response));
        verify(members, times(1)).findOrCreate(token.getClaims());
    }

    @Test void rejectsTokenFromDifferentBrowser() {
        String nonce = controller.config(new MockHttpServletRequest()).getBody().nonce();
        controller.config(request);
        when(decoder.decode("token")).thenReturn(token(nonce));
        var error = assertThrows(ResponseStatusException.class,
                () -> controller.login(new GoogleLoginRequest("token"), request, response));
        assertEquals(401, error.getStatusCode().value());
        verifyNoInteractions(members);
    }

    @Test void rejectsInvalidTokenAndMissingSession() {
        assertThrows(ResponseStatusException.class,
                () -> controller.login(new GoogleLoginRequest("token"), request, response));
        verifyNoInteractions(decoder);
        controller.config(request);
        when(decoder.decode("token")).thenThrow(new BadJwtException("invalid"));
        assertThrows(ResponseStatusException.class,
                () -> controller.login(new GoogleLoginRequest("token"), request, response));
        verifyNoInteractions(members);
    }
}
