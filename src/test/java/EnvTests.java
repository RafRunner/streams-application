import br.rafaelsantana.builders.DotenvBuilder;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EnvTests {

    private static final Dotenv ENV = DotenvBuilder.build();

    @Test
    void hasApiKey() {
        Assertions.assertNotNull(ENV.get("API_KEY"));
    }
}
