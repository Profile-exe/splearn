package tobyspring.splearn;

import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class SplearnApplicationTest {
    @Test
    void run() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            SplearnApplication.main(new String[0]);

            mocked.verify(() -> SpringApplication.run(SplearnApplication.class, new String[0]));
        }
    }
}
