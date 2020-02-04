package com.alpha.domain.user;

import com.alpha.domain.user.dto.UserRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/register")
    public ResponseEntity register(@RequestBody @Valid UserRegisterRequestDto userRegisterRequest) {
        userService.save(userRegisterRequest);
        return ResponseEntity.created(URI.create("/api/login")).build();
    }
}
