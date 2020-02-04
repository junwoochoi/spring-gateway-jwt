package com.alpha.auth.auth;

import com.alpha.member.domain.user.User;
import com.alpha.member.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @PostConstruct
    public void setup() {
        userRepository.save(User.builder()
                .email("junwoochoi")
                .password(passwordEncoder.encode("junwoo"))
                .nickName("nickname")
                .build());
    }

    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword());
        final Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return ResponseEntity.ok(tokenProvider.createToken());
    }
}
