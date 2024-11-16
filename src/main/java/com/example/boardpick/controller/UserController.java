package com.example.boardpick.controller;


import com.example.boardpick.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "users/signupForm";
    }

    @PostMapping("/users/signup")
    public String signup(@Valid UserForm userForm, BindingResult result) {

        log.info("일단 전달받았어");
        if (result.hasErrors()) {
            return "users/signupForm";
        }

        if (!userForm.getPassword1().equals(userForm.getPassword2())) {
            result.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "users/signupForm";
        }
        log.info("error handle");

        try {
            userService.createUser(userForm.getUsername(), userForm.getPassword1());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            result.reject("signupFailed", "이미 등록된 아이디입니다.");
            return "users/signupForm";
        } catch (Exception e) {
            e.printStackTrace();
            result.reject("signupFailed", e.getMessage());
            return "users/signupForm";
        }

        return "redirect:/boardpick";
    }

    @GetMapping("/users/login")
    public String login() {
        return "users/loginForm";
    }
    // 실제 로그인을 구현하는 @PostMapping 방식의 메서드는 스프링 시큐리티가 대신 처리하므로 우리가 구현할 필요가 없다.
}
