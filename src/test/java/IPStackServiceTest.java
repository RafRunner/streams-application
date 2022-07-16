import br.rafaelsantana.services.IPStackService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class IPStackServiceTest {

    @Test
    @Disabled("Limited api calls")
    void successfullyGetsWikipediaIPInformation() {
        var client = IPStackService.buildClient();
        var response = client.getIpInformation("208.80.154.224").join();

        Assertions.assertEquals(response.ip, "208.80.154.224");
        Assertions.assertEquals(response.country, "United States");
        Assertions.assertEquals(response.region, "Virginia");
        Assertions.assertEquals(response.city, "Herndon");
        Assertions.assertNotNull(response.latitude);
        Assertions.assertNotNull(response.longitude);
    }
}
