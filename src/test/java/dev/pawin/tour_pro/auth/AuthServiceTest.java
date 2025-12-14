package dev.pawin.tour_pro.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.model.User;
import dev.pawin.tour_pro.user.repository.UserRepository;
import dev.pawin.tour_pro.user.service.UserService;
import dev.pawin.tour_pro.wallet.WalletService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserLoginRepository userLoginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void whenCreateConsumerCredential() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        UserLogin mockUserLogin = new UserLogin(1, userReference, "test@test.com", "encodedPassword");
        when(userLoginRepository.save(any(UserLogin.class))).thenReturn(mockUserLogin);

        // act
        UserLogin actual = authService.createConsumerCredential(1, mockUserLogin.email(), "encodedPassword");

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.id());
        Assertions.assertEquals(mockUserLogin.email(), actual.email());
        Assertions.assertEquals("encodedPassword", actual.password());

    }

    @Test
    void whenFindCredentialByEmailThenSuccess() {
        // arrange
        String email = "test@test.com";
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        UserLogin mockUserLogin = new UserLogin(1, userReference, email, "encodedPassword");
        when(userLoginRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUserLogin));

        // act
        var actualOpt = authService.findCredentialByEmail(email);

        // assert
        Assertions.assertTrue(actualOpt.isPresent());
        var actual = actualOpt.get();
        Assertions.assertEquals(1, actual.id());
        Assertions.assertEquals(mockUserLogin.email(), actual.email());
        Assertions.assertEquals("encodedPassword", actual.password());

    }

    @Test
    void whenFindCredentialByUserIdThenSuccess() {
        // arrange
        String email = "test@test.com";
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        UserLogin mockUserLogin = new UserLogin(1, userReference, email, "encodedPassword");
        when(userLoginRepository.findByUserId(any(AggregateReference.class))).thenReturn(Optional.of(mockUserLogin));

        // act
        var actualOpt = authService.findCredentialByUserId(mockUser.id());

        // assert
        Assertions.assertTrue(actualOpt.isPresent());
        var actual = actualOpt.get();
        Assertions.assertEquals(1, actual.id());
        Assertions.assertEquals(mockUserLogin.email(), actual.email());
        Assertions.assertEquals("encodedPassword", actual.password());
    }

    @Test
    void whenDeleteCredentialByUserIdThenSuccess() {
        // arrange
        String email = "test@test.com";
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        UserLogin mockUserLogin = new UserLogin(1, userReference, email, "encodedPassword");
        when(userLoginRepository.findByUserId(any(AggregateReference.class))).thenReturn(Optional.of(mockUserLogin));

        // act
        authService.deleteCredentialByUserId(1);

        // assert
        verify(userLoginRepository, times(1)).delete(mockUserLogin);
    }

    @Test
    void whenDeleteCredentialByUserIdButNotFoundThenError() {
        // arrange
        String email = "test@test.com";
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        UserLogin mockUserLogin = new UserLogin(1, userReference, email, "encodedPassword");
        when(userLoginRepository.findByUserId(any(AggregateReference.class))).thenReturn(Optional.empty());

        // act & assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> authService.deleteCredentialByUserId(1));

        verify(userLoginRepository, never()).delete(any());
    }

}
