package dev.pawin.tour_pro.user.service;

import dev.pawin.tour_pro.user.dto.CreateUserDto;
import dev.pawin.tour_pro.user.dto.UpdateUserDto;
import dev.pawin.tour_pro.user.dto.UserInfoDto;

public interface UserService {

    UserInfoDto getUserDtoById(Integer id);

    UserInfoDto createUser(CreateUserDto payload);

    UserInfoDto updateUser(Integer id, UpdateUserDto payload);

    boolean deleteUser(Integer id);    
}
