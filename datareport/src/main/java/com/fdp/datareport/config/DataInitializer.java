package com.fdp.datareport.config;

import com.fdp.datareport.services.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleService roleService;

    @PostConstruct
    public void init() {
        System.out.println("Initializing default roles...");
        roleService.initializeRoles();
    }
}