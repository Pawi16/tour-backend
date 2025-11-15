package dev.pawin.tour_pro.user;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.pawin.tour_pro.common.enumeration.RoleEnum;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;
    
    @Test
    public void shouldReturnRoles() {
        var mockRoles = List.of(
            new Role(RoleEnum.CONSUMER.getId(), RoleEnum.CONSUMER.name()),
            new Role(RoleEnum.ADMIN.getId(), RoleEnum.ADMIN.name()),
            new Role(RoleEnum.COMPANY.getId(), RoleEnum.COMPANY.name())
        );
        when(roleRepository.findAll()).thenReturn(mockRoles);
        var actual = roleService.getAllRole();

        Assertions.assertEquals(mockRoles.size(), actual.size());
        Assertions.assertEquals(mockRoles, actual);
    }
}
