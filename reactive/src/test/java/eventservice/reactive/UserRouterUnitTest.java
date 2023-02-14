package eventservice.reactive;

import eventservice.reactive.exception.EmailExistsException;
import eventservice.reactive.exception.UserNotFoundException;
import eventservice.reactive.exception.UsernameExistsException;
import eventservice.reactive.model.ErrorModel;
import eventservice.reactive.model.UserModel;
import eventservice.reactive.router.UserRouter;
import eventservice.reactive.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import javax.validation.ConstraintViolationException;
import static org.mockito.ArgumentMatchers.any;


@WebFluxTest(UserRouter.class)
public class UserRouterUnitTest {


    @Autowired
    WebTestClient webTestClient;

    @MockBean
    UserService userService;


    @Test
    public void findUserDetails() {
        UserModel userModel = new UserModel("63e66190fee6a77bc97185aa", "user123",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.findUserDetails(any())).thenReturn(Mono.just(userModel));

        webTestClient.get().uri("/api/v3/users/user123")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    public void findUserDetails_nonexistentUser() {
        Mockito.when(userService.findUserDetails(any())).thenReturn(Mono.error(UserNotFoundException::new));

        webTestClient.get().uri("/api/v3/users/user123")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void saveUser() {
        UserModel userModel = new UserModel(null, "user123",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.saveUser(any())).thenReturn(Mono.just(userModel));

        webTestClient.post().uri("/api/v3/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    public void saveUser_invalidData() {
        UserModel userModel = new UserModel(null, "u",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.saveUser(any())).thenReturn(Mono.error(new ConstraintViolationException(null)));

        webTestClient.post().uri("/api/v3/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void saveUser_usernameAlreadyExists() {
        UserModel userModel = new UserModel(null, "user123",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.saveUser(any())).thenReturn(Mono.error(new UsernameExistsException()));

        webTestClient.post().uri("/api/v3/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void saveUser_emailAlreadyExists(){
        UserModel userModel = new UserModel(null, "user123",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.saveUser(any())).thenReturn(Mono.error(new EmailExistsException()));

        webTestClient.post().uri("/api/v3/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void editUser(){
        UserModel userModel = new UserModel(null, "user123",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.editUser(any(), any())).thenReturn(Mono.just(userModel));

        webTestClient.put().uri("/api/v3/users/user123")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    public void editUser_invalidData(){
        UserModel userModel = new UserModel(null, "u",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.editUser(any(), any())).thenReturn(Mono.error(new ConstraintViolationException(null)));

        webTestClient.put().uri("/api/v3/users/user123")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void editUser_nonexistentUsername(){
        UserModel userModel = new UserModel(null, "u",
                "email123@gmail.com", "password123", "user");

        Mockito.when(userService.editUser(any(), any())).thenReturn(Mono.error(UserNotFoundException::new));

        webTestClient.put().uri("/api/v3/users/user123")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(userModel), UserModel.class)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorModel.class);
    }


    @Test
    public void deleteUser(){
        Mockito.when(userService.deleteUser(any())).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/v3/users/user123")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    public void deleteUser_nonexistentUsername(){
        Mockito.when(userService.deleteUser(any())).thenReturn(Mono.error(UserNotFoundException::new));

        webTestClient.delete().uri("/api/v3/users/user123")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorModel.class);
    }
}