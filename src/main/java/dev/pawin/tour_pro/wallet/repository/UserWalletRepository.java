package dev.pawin.tour_pro.wallet.repository;

import org.springframework.data.repository.CrudRepository;

import dev.pawin.tour_pro.wallet.model.UserWallet;
import dev.pawin.tour_pro.user.model.User;
import java.util.Optional;

import org.springframework.data.jdbc.core.mapping.AggregateReference;


public interface UserWalletRepository extends CrudRepository<UserWallet, Integer> {
    
    Optional<UserWallet> findByUserId(AggregateReference<User,Integer> userId);
}
