package dev.pawin.tour_pro.user;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRole() {
        var availableRoles = StreamSupport.stream(roleRepository.findAll().spliterator(), false).collect(Collectors.toList());
        logger.info("avalilabeRoles: {}", availableRoles);
        return availableRoles;    
    }

}
