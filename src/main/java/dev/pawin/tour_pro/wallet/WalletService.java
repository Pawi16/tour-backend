package dev.pawin.tour_pro.wallet;

import java.util.Optional;

import dev.pawin.tour_pro.wallet.model.UserWallet;

public interface WalletService {
    UserWallet createUserWallet(int userId);

    Optional<UserWallet> findUserWalletByUserId (int userId);

    void deleteUserWalletByUserId(int userId);
}
