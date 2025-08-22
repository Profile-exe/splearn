package tobyspring.splearn.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ProfileTest {
    @Test
    void profile() {
        assertThatNoException().isThrownBy(() -> new Profile("dding"));
        assertThatNoException().isThrownBy(() -> new Profile("12345"));
        assertThatNoException().isThrownBy(() -> new Profile("test2025"));
        assertThatNoException().isThrownBy(() -> new Profile(""));
    }

    @Test
    void profileFail() {
        assertThatThrownBy(() -> new Profile("toolongtoolongtoolong"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("A"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Profile("프로필"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void url() {
        var profile = new Profile("dding");

        assertThat(profile.url()).isEqualTo("@dding");
    }
}
