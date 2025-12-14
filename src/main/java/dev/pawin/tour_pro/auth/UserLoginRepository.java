package dev.pawin.tour_pro.auth;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import dev.pawin.tour_pro.user.model.User;
import org.springframework.data.jdbc.core.mapping.AggregateReference;


public interface UserLoginRepository extends CrudRepository<UserLogin, Integer> {
    Optional<UserLogin> findByEmail(String email);

    Optional<UserLogin> findByUserId(AggregateReference<User,Integer> userId);
}
