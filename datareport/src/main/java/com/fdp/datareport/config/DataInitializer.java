package com.fdp.datareport.config;

import com.fdp.datareport.services.RoleService;
import com.fdp.datareport.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleService roleService;
    private final UserService userService;

    @PostConstruct
    public void init() {
        System.out.println("Initializing default roles...");
        roleService.initializeRoles();

        System.out.println("Initializing default admin user...");
        userService.initializeDefaultUser();
    }
}
