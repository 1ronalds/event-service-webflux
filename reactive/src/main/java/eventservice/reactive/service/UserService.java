package eventservice.reactive.service;

import eventservice.reactive.exception.UsernameExistsException;
import eventservice.reactive.model.UserModel;
import eventservice.reactive.exception.EmailExistsException;
import eventservice.reactive.exception.UserNotFoundException;
import eventservice.reactive.repository.UserRepository;
import eventservice.reactive.utils.UserRoleConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final Validator validator;

    public Mono<UserModel> findUserDetails(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(UserNotFoundException::new));
    }


    public Mono<UserModel> saveUser(UserModel userModel) {
        Set<ConstraintViolation<UserModel>> violations = validator.validate(userModel);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return userRepository.findByUsername(userModel.getUsername()).hasElement().flatMap(hasUsername -> {
            if (hasUsername) {
                return Mono.error(UsernameExistsException::new);
            }
            return userRepository.findByEmail(userModel.getEmail()).hasElement().flatMap(hasEmail -> {
                if (hasEmail) {
                    return Mono.error(EmailExistsException::new);
                }
                userModel.setRole(UserRoleConstants.USER);
                return userRepository.save(userModel);

            });
        });
    }


    public Mono<UserModel> editUser(UserModel userModel, String username){
        Set<ConstraintViolation<UserModel>> violations = validator.validate(userModel);
        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }
        return findUserDetails(username)
                .switchIfEmpty(Mono.error(UsernameExistsException::new))
                .flatMap(oldUserDetails -> {
                    userModel.setRole(oldUserDetails.getRole());
                    if(userModel.getPassword() == null) {
                            userModel.setPassword(userModel.getPassword());
                    }
                    return userRepository.save(userModel);
                });
    }


    public Mono<Void> deleteUser(String username){
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(UserNotFoundException::new))
                .flatMap(user -> userRepository.deleteById(user.getId()));
    }
}
