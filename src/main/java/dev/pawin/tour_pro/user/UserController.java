package dev.pawin.tour_pro.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.pawin.tour_pro.user.dto.CreateUserDto;
import dev.pawin.tour_pro.user.dto.UpdateUserDto;
import dev.pawin.tour_pro.user.dto.UserInfoDto;
import dev.pawin.tour_pro.user.service.UserService;
import dev.pawin.tour_pro.user.service.UserServiceImpl;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDto> getUserById(@PathVariable Integer id) {
        UserInfoDto result = userService.getUserDtoById(id);
        logger.info("Get user by id: {}", result);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<UserInfoDto> createUser(@RequestBody @Validated CreateUserDto body) {
        UserInfoDto result = userService.createUser(body);
        logger.info("Create user: {}", result);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.id())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserInfoDto> updateUser(@PathVariable Integer id, @RequestBody @Validated UpdateUserDto body) {
        UserInfoDto result = userService.updateUser(id, body);
        logger.info("Update user by id: {} -> {}", id, result);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        logger.info("Delete user by id: {}", id);
        return ResponseEntity.ok(true);
    }

}
