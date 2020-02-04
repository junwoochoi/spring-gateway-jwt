package com.alpha.domain.user.dto;

import com.alpha.member.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserRegisterRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String nickName;
    @NotBlank
    private String password;

    private String encodedPassword;

    public User toEntity(PasswordEncoder passwordEncoder) {
        if (this.encodedPassword == null) {
            this.initEncodedPassword(passwordEncoder);
        }
        return User.builder()
                .email(this.email)
                .nickName(nickName)
                .password(this.encodedPassword)
                .build();
    }

    private void initEncodedPassword(PasswordEncoder passwordEncoder) {
        this.encodedPassword = passwordEncoder.encode(this.password);
    }
}
