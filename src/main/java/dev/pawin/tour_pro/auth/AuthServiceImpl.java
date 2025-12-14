package dev.pawin.tour_pro.auth;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.model.User;

@Service
public class AuthServiceImpl implements AuthService {

    private final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserLoginRepository userLoginRepository, PasswordEncoder passwordEncoder) {
        this.userLoginRepository = userLoginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserLogin> findCredentialByEmail(String email) {
        return userLoginRepository.findByEmail(email);
    }

    @Override
    public UserLogin createConsumerCredential(int userId, String email, String password) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        String encodedPassword = passwordEncoder.encode(password);
        UserLogin credential = new UserLogin(null, userReference, email, encodedPassword);
        UserLogin userLogin = userLoginRepository.save(credential);
        logger.info("Created credential for Consumer: {}", userLogin.id());
        return userLogin;
    }

    @Override
    public Optional<UserLogin> findCredentialByUserId(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        return userLoginRepository.findByUserId(userReference);

    }

    @Override
    public void deleteCredentialByUserId(int userId) {
        var credential = findCredentialByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Credential for user Id: %d not found", userId)));
        userLoginRepository.delete(credential);
    }

}
