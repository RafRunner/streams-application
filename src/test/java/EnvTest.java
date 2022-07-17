import br.rafaelsantana.AppConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EnvTest {

    @Test
    void hasApiKey() {
        Assertions.assertNotNull(AppConfig.API_KEY);
    }
}
