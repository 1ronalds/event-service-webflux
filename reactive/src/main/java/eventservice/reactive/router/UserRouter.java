package eventservice.reactive.router;

import eventservice.reactive.exception.EmailExistsException;
import eventservice.reactive.exception.UserNotFoundException;
import eventservice.reactive.exception.UsernameExistsException;
import eventservice.reactive.model.ErrorModel;
import eventservice.reactive.model.UserModel;
import eventservice.reactive.service.UserService;
import eventservice.reactive.utils.ErrorMessageConstants;
import eventservice.reactive.utils.ErrorStatusConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@RequiredArgsConstructor
@Configuration
public class UserRouter {
    private final UserService service;


    @Bean
    public RouterFunction<ServerResponse> findUserDetails(){
        return route(RequestPredicates.GET("/api/v3/users/{username}"), request -> {
            String username = request.pathVariable("username");
            Mono<UserModel> response = service.findUserDetails(username);
            return response.flatMap(data -> ServerResponse.ok().body(Mono.just(data), UserModel.class))
                    .onErrorResume(err -> {
                        if (err instanceof UserNotFoundException) {
                            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 404, ErrorStatusConstants.NOT_FOUND,
                                            ErrorMessageConstants.USERNAME_NOT_EXIST, request.uri().toString())), ErrorModel.class);
                        } else {
                            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                            ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())), ErrorModel.class);
                        }});
        });
    }


    @Bean
    public RouterFunction<ServerResponse> saveUser() {
        return route(RequestPredicates.POST("/api/v3/users"), request -> {
            Mono<UserModel> userModel = request.bodyToMono(UserModel.class);
            Mono<UserModel> response = userModel.flatMap(service::saveUser);
            return response.flatMap(data -> ServerResponse.ok().body(Mono.just(data), UserModel.class))
                    .onErrorResume(err -> {
                        if (err instanceof UsernameExistsException) {
                            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 400, ErrorStatusConstants.BAD_REQUEST,
                                            ErrorMessageConstants.USERNAME_REGISTERED, request.uri().toString())), ErrorModel.class);
                        } else if (err instanceof EmailExistsException) {
                            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 400, ErrorStatusConstants.BAD_REQUEST,
                                            ErrorMessageConstants.EMAIL_REGISTERED, request.uri().toString())), ErrorModel.class);
                        } else if (err instanceof ConstraintViolationException) {
                            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 400, ErrorStatusConstants.BAD_REQUEST,
                                            err.toString().replaceAll(".+: ", ""), request.uri().toString())), ErrorModel.class);
                        }
                        else {
                            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                            ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())), ErrorModel.class);
                        }});
        });
    }


    @Bean
    public RouterFunction<ServerResponse> editUser(){
        return route(RequestPredicates.PUT("/api/v3/users/{username}"), request -> {
            Mono<UserModel> userModel = request.bodyToMono(UserModel.class);
            String username = request.pathVariable("username");
            Mono<UserModel> response = userModel.flatMap(user -> service.editUser(user, username));
            return response.flatMap(data -> ServerResponse.ok().body(Mono.just(data), UserModel.class))
                    .onErrorResume(err -> {
                        if (err instanceof UserNotFoundException) {
                            return ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 404, ErrorStatusConstants.NOT_FOUND,
                                            ErrorMessageConstants.USERNAME_NOT_EXIST, request.uri().toString())), ErrorModel.class);
                        } else if (err instanceof ConstraintViolationException) {
                            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 400, ErrorStatusConstants.BAD_REQUEST,
                                            err.toString().replaceAll(".+: ", ""), request.uri().toString())), ErrorModel.class);
                        } else {
                            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                            ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())), ErrorModel.class);
                        }});
        });
    }


    @Bean
    public RouterFunction<ServerResponse> deleteUser() {
        return route(RequestPredicates.DELETE("/api/v3/users/{username}"), request -> {
            String username = request.pathVariable("username");
            Mono<Void> response = service.deleteUser(username);
            return response.flatMap(data -> ServerResponse.ok().build())
                    .onErrorResume(err -> {
                        if (err instanceof UserNotFoundException) {
                            return ServerResponse.status(HttpStatus.NOT_FOUND)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 404, ErrorStatusConstants.NOT_FOUND,
                                            ErrorMessageConstants.USERNAME_NOT_EXIST, request.uri().toString())), ErrorModel.class);
                        } else {
                            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body(Mono.just(new ErrorModel(LocalDate.now(), 500, ErrorStatusConstants.INTERNAL_SERVER_ERROR,
                                            ErrorMessageConstants.INTERNAL_SERVER_ERROR, request.uri().toString())), ErrorModel.class);
                        }
                    });
        });
    }


}
