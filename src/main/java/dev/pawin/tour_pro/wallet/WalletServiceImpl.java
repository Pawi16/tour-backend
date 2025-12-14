package dev.pawin.tour_pro.wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.stereotype.Service;

import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.tour_company.model.TourCompany;
import dev.pawin.tour_pro.user.model.User;
import dev.pawin.tour_pro.wallet.model.TourCompanyWallet;
import dev.pawin.tour_pro.wallet.model.UserWallet;
import dev.pawin.tour_pro.wallet.repository.UserWalletRepository;

@Service
public class WalletServiceImpl implements WalletService {

    private final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);
    private final UserWalletRepository userWalletRepository;

    public WalletServiceImpl(UserWalletRepository userWalletRepository) {
        this.userWalletRepository = userWalletRepository;
    }

    @Override
    public UserWallet createUserWallet(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        Instant currentTimestamp = Instant.now();
        BigDecimal initBalance = new BigDecimal("0.00");
        UserWallet userWallet = new UserWallet(null, userReference, currentTimestamp, initBalance);
        UserWallet savedUserWallet = userWalletRepository.save(userWallet);
        logger.info("Created wallet for user: {}", userId);
        return savedUserWallet;
    }

    @Override
    public Optional<UserWallet> findUserWalletByUserId(int userId) {
        AggregateReference<User, Integer> userReference = AggregateReference.to(userId);
        return userWalletRepository.findByUserId(userReference);
    }

    @Override
    public void deleteUserWalletByUserId(int userId) {
        UserWallet userWallet = findUserWalletByUserId(userId).orElseThrow(() -> new EntityNotFoundException(String.format("Wallet for user Id: %d not found", userId)));
        userWalletRepository.delete(userWallet);
    }
}
