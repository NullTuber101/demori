package com.fdp.datareport.services;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.repositories.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role superUserRole;
    private Role editorRole;
    private Role viewerRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        superUserRole = Role.builder()
                .roleName("SUPER_USER")
                .canApproveUsers(true)
                .canWrite(true)
                .canDelete(true)
                .build();

        editorRole = Role.builder()
                .roleName("EDITOR")
                .canApproveUsers(false)
                .canWrite(true)
                .canDelete(true)
                .build();

        viewerRole = Role.builder()
                .roleName("VIEWER")
                .canApproveUsers(false)
                .canWrite(false)
                .canDelete(false)
                .build();
    }

    @Test
    void testGetRoleByName_whenExists() {
        when(roleRepository.findByRoleName("EDITOR")).thenReturn(Optional.of(editorRole));

        Role result = roleService.getRoleByName("EDITOR");

        assertThat(result).isEqualTo(editorRole);
    }

    @Test
    void testGetRoleByName_whenNotExists() {
        when(roleRepository.findByRoleName("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getRoleByName("UNKNOWN"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");
    }

    @Test
    void testFindByName() {
        when(roleRepository.findByRoleName("VIEWER")).thenReturn(Optional.of(viewerRole));

        Optional<Role> result = roleService.findByName("VIEWER");

        assertThat(result).isPresent().contains(viewerRole);
    }

    @Test
    void testGetAllRoles() {
        when(roleRepository.findAll()).thenReturn(List.of(superUserRole, editorRole, viewerRole));

        List<Role> roles = roleService.getAllRoles();

        assertThat(roles).hasSize(3).containsExactlyInAnyOrder(superUserRole, editorRole, viewerRole);
    }

    @Test
    void testInitializeRoles_whenEmpty() {
        when(roleRepository.count()).thenReturn(0L);

        roleService.initializeRoles();

        verify(roleRepository, times(3)).save(any(Role.class));
    }

    @Test
    void testInitializeRoles_whenAlreadyPresent() {
        when(roleRepository.count()).thenReturn(5L);

        roleService.initializeRoles();

        verify(roleRepository, never()).save(any(Role.class));
    }
}
