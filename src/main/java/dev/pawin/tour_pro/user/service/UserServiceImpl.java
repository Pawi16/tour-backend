package dev.pawin.tour_pro.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import dev.pawin.tour_pro.auth.AuthService;
import dev.pawin.tour_pro.auth.UserLogin;
import dev.pawin.tour_pro.common.exception.CredentialExistsException;
import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.dto.CreateUserDto;
import dev.pawin.tour_pro.user.dto.UpdateUserDto;
import dev.pawin.tour_pro.user.dto.UserInfoDto;
import dev.pawin.tour_pro.user.model.User;
import dev.pawin.tour_pro.user.repository.UserRepository;
import dev.pawin.tour_pro.wallet.WalletService;
import dev.pawin.tour_pro.wallet.model.UserWallet;

@Service
public class UserServiceImpl implements UserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final AuthService authService;

    public UserServiceImpl(UserRepository userRepository, AuthService authService, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.authService = authService;
    }

    private User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User Id: %d not found", id)));
    }

    @Override
    public UserInfoDto getUserDtoById(Integer id) {
        User user = getUserById(id);
        UserInfoDto result = new UserInfoDto(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.phoneNumber());
        return result;
    }

    // Create user + login credential + wallet
    @Override
    @Transactional
    public UserInfoDto createUser(CreateUserDto payload) {
        // Check existing user credential
        var existingUserLogin = authService.findCredentialByEmail(payload.email());
        if (existingUserLogin.isPresent()) {
            throw new CredentialExistsException(String.format("User: %s exists!", payload.email()));
        }
        // Create user
        User user = new User(null, payload.firstName(), payload.lastName(), payload.phoneNumber());
        User savedUser = userRepository.save(user);
        // Create wallet
        UserWallet newWallet = walletService.createUserWallet(savedUser.id());
        // Create user login credential
        UserLogin newCredential = authService.createConsumerCredential(savedUser.id(), payload.email(),
                payload.password());
        return new UserInfoDto(savedUser.id(), savedUser.firstName(), savedUser.lastName(), savedUser.phoneNumber());
    }

    @Override
    public UserInfoDto updateUser(Integer id, UpdateUserDto payload) {
        User user = getUserById(id);
        User updatedUser = new User(
                user.id(),
                payload.firstName(),
                payload.lastName(),
                user.phoneNumber());
        User savedUser = userRepository.save(updatedUser);
        return new UserInfoDto(
                savedUser.id(),
                savedUser.firstName(),
                savedUser.lastName(),
                savedUser.phoneNumber());
    }

    @Override
    @Transactional
    public boolean deleteUser(Integer id) {
        // find exiting user
        User user = getUserById(id);

        // delete user wallet
        walletService.deleteUserWalletByUserId(id);
        logger.info("Delete wallet for user Id: {}", id);

        // delete user login credential
        authService.deleteCredentialByUserId(id);
        logger.info("Delete credential for user Id: {}", id);

        userRepository.delete(user);
        logger.info("Deleted user entity for user Id: {}", id);
        return true;
    }

}
