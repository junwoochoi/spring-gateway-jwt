package com.alpha.domain.user;

import com.alpha.domain.user.dto.UserRegisterRequestDto;
import com.alpha.member.domain.user.User;
import com.alpha.member.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long save(UserRegisterRequestDto userRegisterRequest) {
        final User savedUser = userRepository.save(userRegisterRequest.toEntity(passwordEncoder));
        return savedUser.getId();
    }
}
