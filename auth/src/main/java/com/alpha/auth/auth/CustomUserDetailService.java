package com.alpha.auth.auth;

import com.alpha.auth.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final com.alpha.member.domain.user.User byEmail = userService.findByEmail(username);
        return User.builder()
                .username(username)
                .password(byEmail.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}
