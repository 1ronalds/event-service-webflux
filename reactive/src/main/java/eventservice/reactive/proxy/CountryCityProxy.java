package eventservice.reactive.proxy;

import eventservice.reactive.model.CityModel;
import eventservice.reactive.model.CountryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

@RequiredArgsConstructor
@Component
public class CountryCityProxy {

    public Flux<CountryModel> findCountries(){
        WebClient webClient = WebClient.create("http://localhost:8081/api/country-city-service/v1");
        return webClient.get()
                .uri("/countries")
                .retrieve()
                .bodyToFlux(CountryModel.class)
                .onErrorResume(ConnectException.class, ex -> Flux.error(RuntimeException::new));
    }

    public Flux<CityModel> findCities(String countryId){
        WebClient webClient = WebClient.create("http://localhost:8081/api/country-city-service/v1");
        return webClient.get()
                .uri("/cities/" + countryId)
                .retrieve()
                .bodyToFlux(CityModel.class)
                .onErrorResume(ConnectException.class, ex -> Flux.error(RuntimeException::new));

    }
}