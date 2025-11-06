package dev.pawin.tour_pro.user;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class TestController {
    private final RoleService roleService;

    public TestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/role")
    public List<Role> getAllRole() {
        return roleService.getAllRole();
    }
    
}
