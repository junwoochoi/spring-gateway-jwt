package com.alpha.auth.auth;

import com.alpha.member.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenAuthenticationManager refreshTokenAuthenticationManager;

    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationRequestDto authenticationRequestDto) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(), authenticationRequestDto.getPassword());
        final Authentication authentication = authenticationManager.authenticate(token);

        final TokenUser tokenUser = new TokenUser(authentication);
        return ResponseEntity.ok(tokenProvider.createToken(tokenUser));
    }


    @PostMapping("/api/login/refreshtoken")
    public ResponseEntity regenerateToken(@RequestHeader("grant_type") String grantType, @RequestParam("refresh_token") String refreshToken) {
        if (!"refresh_token".equals(grantType)) {
            return ResponseEntity.badRequest().body("grant_type invalid");
        }

        final TokenUser tokenUser = refreshTokenAuthenticationManager.authenticate(refreshToken);
        return ResponseEntity.ok(tokenProvider.createToken(tokenUser));
    }


}
