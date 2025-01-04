package com.example.boardpick.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserForm {

    @Size(min=3, max=10)
    @NotEmpty(message = "회원 이름은 필수입니다.")
    private String username;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호를 확인해주세요.")
    private String password2;

}
