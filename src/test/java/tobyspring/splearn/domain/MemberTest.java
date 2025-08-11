package tobyspring.splearn.domain;


import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemberTest {
    Member member;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder() {
            @Override
            public String encode(String password) {
                return password.toUpperCase();
            }

            @Override
            public boolean matches(String password, String passwordHash) {
                return encode(password).equals(passwordHash);
            }
        };
        member = Member.create("test@splearn.app", "DDING", "secret", passwordEncoder);
    }

    @Test
    void createMember() {
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
    }

    @Test
    void activate() {
        member.activate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    void activateFail() {
        member.activate();

        assertThatThrownBy(member::activate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("PENDING 상태가 아닙니다");
    }

    @Test
    void deactivate() {
        member.activate();

        member.deactivate();

        assertThat(member.getStatus()).isEqualTo(MemberStatus.DEACTIVATED);
    }

    @Test
    void deactivateFail() {
        assertThatThrownBy(member::deactivate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ACTIVE 상태가 아닙니다");

        member.activate();
        member.deactivate();

        assertThatThrownBy(member::deactivate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("ACTIVE 상태가 아닙니다");
    }

    @Test
    void verifyPassword() {
        assertThat(member.verifyPassword("secret", passwordEncoder)).isTrue();
        assertThat(member.verifyPassword("hello", passwordEncoder)).isFalse();
    }

    @Test
    void changeNickname() {
        assertThat(member.getNickname()).isEqualTo("DDING");

        member.changeNickname("wow");

        assertThat(member.getNickname()).isEqualTo("wow");
    }

    @Test
    void changePassword() {
        member.changePassword("changeSecret", passwordEncoder);

        assertThat(member.verifyPassword("changeSecret", passwordEncoder)).isTrue();
    }
}
