package dev.pawin.tour_pro.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import javax.security.auth.login.CredentialNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.pawin.tour_pro.auth.AuthService;
import dev.pawin.tour_pro.auth.UserLogin;
import dev.pawin.tour_pro.common.exception.CredentialExistsException;
import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.dto.CreateUserDto;
import dev.pawin.tour_pro.user.dto.UpdateUserDto;
import dev.pawin.tour_pro.user.dto.UserInfoDto;
import dev.pawin.tour_pro.user.model.User;
import dev.pawin.tour_pro.user.repository.UserRepository;
import dev.pawin.tour_pro.user.service.UserService;
import dev.pawin.tour_pro.user.service.UserServiceImpl;
import dev.pawin.tour_pro.wallet.WalletService;
import dev.pawin.tour_pro.wallet.model.UserWallet;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private AuthService authService;

    @Test
    void whenGetUserDtoByIdThenSuccessfull() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        // act
        UserInfoDto actual = userService.getUserDtoById(1);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(mockUser.id(), actual.id());
        Assertions.assertEquals(mockUser.firstName(), actual.firstName());
        Assertions.assertEquals(mockUser.lastName(), actual.lastName());
        Assertions.assertEquals(mockUser.phoneNumber(), actual.phoneNumber());
    }

    @Test
    void whenGetUserDtoByIdButNotFoundThenError() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // act
        Assertions.assertThrows(EntityNotFoundException.class, () -> userService.getUserDtoById(1));
    }

    @Test
    void whenCreateUserThenSuccess() {
        // arrange
        CreateUserDto payload = new CreateUserDto("testName", "testLastName", "0899999999", "test@test.com",
                "testPassword");
        User mockUser = new User(1, payload.firstName(), payload.lastName(), payload.phoneNumber());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        Instant now = Instant.now();
        UserWallet mockUserWallet = new UserWallet(1, userReference, now, new BigDecimal("0.00"));
        when(walletService.createUserWallet(mockUser.id())).thenReturn(mockUserWallet);

        UserLogin mockUserLogin = new UserLogin(1, userReference, payload.email(), "encodedPassword");
        when(authService.createConsumerCredential(anyInt(), anyString(), anyString())).thenReturn(mockUserLogin);

        // act
        UserInfoDto actual = userService.createUser(payload);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.id());
        Assertions.assertEquals(payload.firstName(), actual.firstName());

        verify(userRepository).save(any(User.class));
        verify(walletService).createUserWallet(1);
        verify(authService).createConsumerCredential(1, payload.email(), payload.password());

    }

    @Test 
    void whenCreateUserButUserExistsThenFail() {
        // arrange
        CreateUserDto payload = new CreateUserDto("testName", "testLastName", "0899999999", "test@test.com", "testPassword");
        User mockUser = new User(1, payload.firstName(), payload.lastName(), payload.phoneNumber());
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());

        UserLogin mockUserLogin = new UserLogin(1, userReference, payload.email(), "encodedPassword");
        when(authService.findCredentialByEmail(anyString())).thenReturn(Optional.of(mockUserLogin));

        // act & assert
        Assertions.assertThrows(CredentialExistsException.class, () -> userService.createUser(payload));

        verify(userRepository, never()).save(any());
        verify(walletService, never()).createUserWallet(anyInt());
    }

    @Test
    void whenUpdateUserThenSuccess() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        int userId = 1;
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        UpdateUserDto payload = new UpdateUserDto("newName", "newLastName");
        User updatedUserMock = new User(userId, payload.firstName(), payload.lastName(), "0811111111");
        when(userRepository.save(any(User.class))).thenReturn(updatedUserMock);

        // act
        UserInfoDto actual = userService.updateUser(userId, payload);

        // assert
        Assertions.assertEquals(updatedUserMock.firstName(), actual.firstName());
        Assertions.assertEquals(updatedUserMock.lastName(), actual.lastName());

    }

    @Test
    void whenDeleteUserThenSuccess() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        int userId = 1;
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(mockUser));

        // act
        boolean actual = userService.deleteUser(userId);

        // assert
        Assertions.assertTrue(actual);
        verify(walletService).deleteUserWalletByUserId(userId);
        verify(authService).deleteCredentialByUserId(userId);
        verify(userRepository).delete(mockUser);

    }

}
