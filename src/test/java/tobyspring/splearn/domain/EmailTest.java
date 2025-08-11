package tobyspring.splearn.domain;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailTest {

    @Test
    void equality() {
        var email1 = new Email("dding@example.com");
        var email2 = new Email("dding@example.com");

        assertThat(email1).isEqualTo(email2);
    }

    @Test
    void validEmail() {
        assertThatNoException()
                .isThrownBy(() -> new Email("test@example.com"));
    }

    @Test
    void invalidEmail() {
        assertThatThrownBy(() -> new Email("invalid email"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
