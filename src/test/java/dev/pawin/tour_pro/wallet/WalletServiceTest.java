package dev.pawin.tour_pro.wallet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jdbc.core.mapping.AggregateReference;

import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.model.User;
import dev.pawin.tour_pro.wallet.model.UserWallet;
import dev.pawin.tour_pro.wallet.repository.UserWalletRepository;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {
    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private UserWalletRepository userWalletRepository;

    @Test
    void whenCreatedUserWalletThenSuccessful() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        Instant now = Instant.now();
        UserWallet savedUserWallet = new UserWallet(1, userReference, now, new BigDecimal("0.00"));
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(savedUserWallet);

        // act
        var actual = walletService.createUserWallet(1);

        // assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userReference, actual.userId());
        Assertions.assertEquals(new BigDecimal("0.00"), actual.balance());
        Assertions.assertEquals(now, actual.lastUpdated());
    }

    @Test
    void whenFindUserWalletByUserIdThenSuccessful() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        Instant now = Instant.now();
        UserWallet mockUserWallet = new UserWallet(1, userReference, now, new BigDecimal("0.00"));
        when(userWalletRepository.findByUserId(userReference)).thenReturn(Optional.of(mockUserWallet));

        // act
        var actualOpt = walletService.findUserWalletByUserId(1);
        // assert
        Assertions.assertTrue(actualOpt.isPresent());
        UserWallet actual = actualOpt.get();
        Assertions.assertEquals(1, actual.id());
        Assertions.assertEquals(userReference, actual.userId());
        Assertions.assertEquals(new BigDecimal("0.00"), actual.balance());
        Assertions.assertEquals(now, actual.lastUpdated());
    }

    @Test
    void whenDeleteUserWalletByUserIdThenSuccessful() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        Instant now = Instant.now();
        UserWallet mockUserWallet = new UserWallet(1, userReference, now, new BigDecimal("0.00"));
        when(userWalletRepository.findByUserId(userReference)).thenReturn(Optional.of(mockUserWallet));

        // act
        walletService.deleteUserWalletByUserId(1);

        verify(userWalletRepository, times(1)).delete(mockUserWallet);
    }

    @Test
    void whenDeleteUserWalletByUserIdButNotFoundThenError() {
        // arrange
        User mockUser = new User(1, "test", "test", "0899999999");
        AggregateReference<User, Integer> userReference = AggregateReference.to(mockUser.id());
        Instant now = Instant.now();
        UserWallet mockUserWallet = new UserWallet(1, userReference, now, new BigDecimal("0.00"));

        when(userWalletRepository.findByUserId(any())).thenReturn(Optional.empty());

        // act & assert
        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            walletService.deleteUserWalletByUserId(1);
        });

        // assert
        verify(userWalletRepository, never()).delete(any());
    }
}
