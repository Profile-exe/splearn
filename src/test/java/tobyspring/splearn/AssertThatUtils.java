package tobyspring.splearn;

import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import org.assertj.core.api.AssertProvider;
import org.assertj.core.api.Assertions;
import org.springframework.test.json.JsonPathValueAssert;

@UtilityClass
public class AssertThatUtils {
    public static Consumer<AssertProvider<JsonPathValueAssert>> notNull() {
        return value -> Assertions.assertThat(value).isNotNull();
    }

    public static Consumer<AssertProvider<JsonPathValueAssert>> equalsTo(String stringValue) {
        return value -> Assertions.assertThat(value).isEqualTo(stringValue);
    }
}
