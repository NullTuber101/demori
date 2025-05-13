package com.fdp.datareport.config;

import com.fdp.datareport.entities.User;
import com.fdp.datareport.services.RoleService;
import com.fdp.datareport.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
class DataInitializerTest {

    private RoleService roleService;
    private UserService userService;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        roleService = mock(RoleService.class);
        userService = mock(UserService.class);
        dataInitializer = new DataInitializer(roleService, userService);
    }

    @Test
    void testInit_shouldInitializeRolesAndUser() {
        dataInitializer.init();

        verify(roleService, times(1)).initializeRoles();
        verify(userService, times(1)).initializeDefaultUser();
    }
}
