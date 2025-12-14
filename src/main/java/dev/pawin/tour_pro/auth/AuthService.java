package dev.pawin.tour_pro.auth;

import java.util.Optional;

public interface AuthService {
    
    Optional<UserLogin> findCredentialByEmail(String email);

    Optional<UserLogin> findCredentialByUserId(int userId);

    UserLogin createConsumerCredential(int userId, String email, String password);

    void deleteCredentialByUserId(int userId);
}
