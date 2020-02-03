package com.alpha.auth.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name = "email", nullable = false, updatable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Builder
    public User(String nickName, String email, String password) {
        this.email = email;
        this.nickName = nickName;
        this.password = password;
    }
}
