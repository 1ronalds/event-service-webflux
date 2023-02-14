package eventservice.reactive;

import eventservice.reactive.exception.EmailExistsException;
import eventservice.reactive.exception.UserNotFoundException;
import eventservice.reactive.exception.UsernameExistsException;
import eventservice.reactive.model.UserModel;
import eventservice.reactive.repository.UserRepository;
import eventservice.reactive.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(SpringExtension.class)
public class UserServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    Validator validator;

    @InjectMocks
    UserService userService;


    @Test
    public void findUserDetails(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(userRepository.findByUsername("Ronalds")).thenReturn(Mono.just(userModel));

        Mono<UserModel> result = userService.findUserDetails("Ronalds");

        StepVerifier.create(result).expectNext(userModel).verifyComplete();
    }


    @Test
    public void findUserDetails_nonexistentUser(){
        Mockito.when(userRepository.findByUsername("Ronalds")).thenReturn(Mono.empty());

        Mono<UserModel> result = userService.findUserDetails("Ronalds");

        StepVerifier.create(result).expectError(UserNotFoundException.class).verify();
    }


    @Test
    public void saveUser(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(validator.validate(userModel)).thenReturn(Collections.emptySet());
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.empty());
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(any())).thenReturn(Mono.just(userModel));

        Mono<UserModel> result = userService.saveUser(userModel);
        StepVerifier.create(result).expectNext(userModel).verifyComplete();
    }


    @Test
    public void saveUser_invalidData(){
        UserModel userModel = new UserModel(null, "R",
                "ronalds@gmail.com", "password123", "user");

        Set<ConstraintViolation<UserModel>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        Mockito.when(validator.validate(userModel)).thenReturn(violations);

        Mono<UserModel> result = userService.saveUser(userModel);
        StepVerifier.create(result).expectError(ConstraintViolationException.class).verify();
    }


    @Test
    public void saveUser_existingUsername(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(validator.validate(userModel)).thenReturn(Collections.emptySet());
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.just(userModel));

        Mono<UserModel> result = userService.saveUser(userModel);
        StepVerifier.create(result).expectError(UsernameExistsException.class).verify();

    }


    @Test
    public void saveUser_existingEmail(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(validator.validate(userModel)).thenReturn(Collections.emptySet());
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.empty());
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Mono.just(userModel));

        Mono<UserModel> result = userService.saveUser(userModel);
        StepVerifier.create(result).expectError(EmailExistsException.class).verify();
    }


    @Test
    public void editUser(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(validator.validate(userModel)).thenReturn(Collections.emptySet());
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.just(userModel));
        Mockito.when(userRepository.findByEmail(any())).thenReturn(Mono.empty());
        Mockito.when(userRepository.save(any())).thenReturn(Mono.just(userModel));

        Mono<UserModel> result = userService.editUser(userModel, "Ronalds");
        StepVerifier.create(result).expectNext(userModel).verifyComplete();
    }


    @Test
    public void editUser_invalidData(){
        UserModel userModel = new UserModel(null, "R",
                "ronalds@gmail.com", "password123", "user");

        Set<ConstraintViolation<UserModel>> violations = new HashSet<>();
        violations.add(mock(ConstraintViolation.class));
        Mockito.when(validator.validate(userModel)).thenReturn(violations);

        Mono<UserModel> result = userService.editUser(userModel, "Ronalds");
        StepVerifier.create(result).expectError(ConstraintViolationException.class).verify();
    }


    @Test
    public void editUser_nonexistentUsername(){
        UserModel userModel = new UserModel(null, "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(validator.validate(userModel)).thenReturn(Collections.emptySet());
        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.empty());

        Mono<UserModel> result = userService.editUser(userModel, "Ronalds");
        StepVerifier.create(result).expectError(UserNotFoundException.class).verify();
    }


    @Test
    public void deleteUser(){
        UserModel userModel = new UserModel("63e65d4ef255b4533f4ad6ad", "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.just(userModel));
        Mockito.when(userRepository.deleteById(anyString())).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteUser("Ronalds");
        StepVerifier.create(result).expectComplete().verify();
        Mockito.verify(userRepository, times(1)).deleteById(userModel.getId());

    }


    @Test
    public void deleteUser_nonexistentUsername(){
        UserModel userModel = new UserModel("63e65d4ef255b4533f4ad6ad", "Ronalds",
                "ronalds@gmail.com", "password123", "user");

        Mockito.when(userRepository.findByUsername(any())).thenReturn(Mono.empty());

        Mono<Void> result = userService.deleteUser("Ronalds");
        StepVerifier.create(result).expectError(UserNotFoundException.class).verify();
    }
}
