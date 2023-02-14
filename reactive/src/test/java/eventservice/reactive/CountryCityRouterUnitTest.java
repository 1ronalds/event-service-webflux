package eventservice.reactive;

import eventservice.reactive.model.CityModel;
import eventservice.reactive.model.CountryModel;
import eventservice.reactive.model.ErrorModel;
import eventservice.reactive.proxy.CountryCityProxy;
import eventservice.reactive.router.CountryCityRouter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import static org.mockito.ArgumentMatchers.any;


@WebFluxTest(CountryCityRouter.class)
public class CountryCityRouterUnitTest {


    @Autowired
    WebTestClient webTestClient;

    @MockBean
    CountryCityProxy countryCityProxy;


    @Test
    public void findCountries(){

        CountryModel countryModel = new CountryModel(1L, "Algeria");

        Mockito.when(countryCityProxy.findCountries()).thenReturn(Flux.just(countryModel));

        webTestClient.get().uri("/api/v3/countries")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void findCountries_disconnectedServer() {
        Mockito.when(countryCityProxy.findCountries()).thenReturn(Flux.error(new RuntimeException()));

        webTestClient.get().uri("/api/v3/countries").exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorModel.class);
    }


    @Test
    void findCities(){
        CityModel cityModel = new CityModel("La");

        Mockito.when(countryCityProxy.findCities(any())).thenReturn(Flux.just(cityModel));

        webTestClient.get().uri("/api/v3/cities/1")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void findCities_disconnectedServer(){
        Mockito.when(countryCityProxy.findCities(any())).thenReturn(Flux.error(RuntimeException::new));

        webTestClient.get().uri("/api/v3/cities/1")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorModel.class);
    }

}
