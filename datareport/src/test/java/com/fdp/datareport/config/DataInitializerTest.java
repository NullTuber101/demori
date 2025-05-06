package com.fdp.datareport.config;

import com.fdp.datareport.services.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class DataInitializerTest {

    private RoleService roleService;
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        roleService = mock(RoleService.class);
        dataInitializer = new DataInitializer(roleService); // Uses Lombok's @RequiredArgsConstructor
    }

    @Test
    void testInit_shouldInitializeRoles() {
        dataInitializer.init();

        verify(roleService, times(1)).initializeRoles();
    }
}
