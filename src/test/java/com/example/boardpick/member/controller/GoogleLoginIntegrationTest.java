package com.example.boardpick.member.controller;

import com.example.boardpick.list.repository.GameListRepository;
import com.example.boardpick.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GoogleLoginIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired MemberRepository members;
    @Autowired GameListRepository lists;
    @MockBean JwtDecoder decoder;

    @Test void signupReturningLoginMeAndLogout() throws Exception {
        long memberCount = members.count();
        long listCount = lists.count();
        MockHttpSession session = null;
        for (int attempt = 0; attempt < 2; attempt++) {
            var config = mvc.perform(get("/api/oauth2/google/config"))
                    .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                    .andReturn();
            session = (MockHttpSession) config.getRequest().getSession();
            String nonce = json.readTree(config.getResponse().getContentAsString()).get("nonce").asText();
            when(decoder.decode("valid-token")).thenReturn(Jwt.withTokenValue("valid-token")
                    .header("alg", "RS256").subject("integration-user")
                    .claim("nonce", nonce).claim("name", "테스트 회원").build());
            mvc.perform(post("/api/oauth2/google").session(session).contentType("application/json")
                            .content("{\"idToken\":\"valid-token\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("loginId").value("google_integration-user"));
            mvc.perform(get("/api/members/me").session(session))
                    .andExpect(status().isOk()).andExpect(jsonPath("loginId").value("google_integration-user"));
            mvc.perform(post("/api/oauth2/google").session(session).contentType("application/json")
                            .content("{\"idToken\":\"valid-token\"}"))
                    .andExpect(status().isUnauthorized());
            assertEquals(memberCount + 1, members.count());
            assertEquals(listCount + 1, lists.count());
        }
        mvc.perform(delete("/api/members/login").session(session)).andExpect(status().isNoContent());
        assertTrue(session.isInvalid());
        mvc.perform(get("/api/members/me")).andExpect(status().isUnauthorized());
    }

    @Test void rejectsEmptyToken() throws Exception {
        mvc.perform(post("/api/oauth2/google").contentType("application/json").content("{\"idToken\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
