package dev.pawin.tour_pro.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import dev.pawin.tour_pro.common.exception.EntityNotFoundException;
import dev.pawin.tour_pro.user.dto.CreateUserDto;
import dev.pawin.tour_pro.user.dto.UpdateUserDto;
import dev.pawin.tour_pro.user.dto.UserInfoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.pawin.tour_pro.user.service.UserService;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenGetUserByIdThenSuccessful() throws Exception {
        var mockUserInfoDto = new UserInfoDto(1, "Test", "TestLastName", "0000000001");
        when(userService.getUserDtoById(anyInt())).thenReturn(mockUserInfoDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockUserInfoDto.id()))
                .andExpect(jsonPath("$.firstName").value(mockUserInfoDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(mockUserInfoDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(mockUserInfoDto.phoneNumber()));
    }

    @Test
    void whenGetUserByIdButNotFoundThenError() throws Exception {
        int nonExistentId = 999;
        when(userService.getUserDtoById(nonExistentId))
                .thenThrow(new EntityNotFoundException(String.format("User Id: %s not found", nonExistentId)));

        // act & assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateUserThenSuccessful() throws Exception {
        var mockUserInfoDto = new UserInfoDto(1, "Test", "TestLastName", "0000000001");
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(mockUserInfoDto);
        CreateUserDto payload = new CreateUserDto(mockUserInfoDto.firstName(), mockUserInfoDto.lastName(),
                mockUserInfoDto.lastName(), "test@email.com", "testPassword");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockUserInfoDto.id()))
                .andExpect(jsonPath("$.firstName").value(mockUserInfoDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(mockUserInfoDto.lastName()))
                .andExpect(jsonPath("$.phoneNumber").value(mockUserInfoDto.phoneNumber()));
    }

    @Test
    void whenUpdateUserThenSuccessful() throws Exception {
        var mockUserInfoDto = new UserInfoDto(1, "Test", "TestLastName", "0000000001");
        when(userService.updateUser(anyInt(),any(UpdateUserDto.class))).thenReturn(mockUserInfoDto);
        UpdateUserDto payload = new UpdateUserDto(mockUserInfoDto.firstName(), mockUserInfoDto.lastName());
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(mockUserInfoDto.firstName()))
                .andExpect(jsonPath("$.lastName").value(mockUserInfoDto.lastName()));
    }

    @Test
    void whenDeleteUserThenSuccessful() throws Exception {
        when(userService.deleteUser(anyInt())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/users/{id}", 1))
        .andExpect(status().isOk());
    }
}
