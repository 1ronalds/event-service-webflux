package eventservice.reactive.repository;

import eventservice.reactive.model.UserModel;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<UserModel, String> {

    Mono<UserModel> findByUsername(String username);
    Mono<UserModel> findByEmail(String email);

}
