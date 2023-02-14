package eventservice.reactive.router;

import eventservice.reactive.model.CityModel;
import eventservice.reactive.model.CountryModel;
import eventservice.reactive.model.ErrorModel;
import eventservice.reactive.proxy.CountryCityProxy;
import eventservice.reactive.utils.ErrorMessageConstants;
import eventservice.reactive.utils.ErrorStatusConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class CountryCityRouter {
    private final CountryCityProxy countryCityProxy;

    @Bean
    public RouterFunction<ServerResponse> findCountries() {
        return route(RequestPredicates.GET("/api/v3/countries"), request -> {
            Flux<CountryModel> response = countryCityProxy.findCountries();
            return response.collectList().flatMap(data -> ServerResponse.ok().body(Mono.just(data), CountryModel.class))
                    .onErrorResume(ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                    ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())));
        });
    }


    @Bean
    public RouterFunction<ServerResponse> findCities() {
        return route(RequestPredicates.GET("/api/v3/cities/{cityId}"), request -> {
            String countryId = request.pathVariable("cityId");
            Flux<CityModel> response = countryCityProxy.findCities(countryId);
            return response.collectList().flatMap(data -> ServerResponse.ok().body(Mono.just(data), CityModel.class))
                    .onErrorResume(ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .bodyValue(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                    ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())));
        });
    }
}

