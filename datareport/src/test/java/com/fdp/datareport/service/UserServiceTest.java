package com.fdp.datareport.service;

import com.fdp.datareport.entity.Role;
import com.fdp.datareport.entity.User;
import com.fdp.datareport.entity.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private UserRequestService userRequestService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private User user;
    private Role role;
    private UserRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = Role.builder().id(1L).roleName("EDITOR").build();
        user = User.builder().id(1L).brid("BR123").email("test@example.com").password("pass").role(role).build();
        request = UserRequest.builder().id(1L).brid("BR123").email("test@example.com").password("plain").name("Test").build();
    }

    @Test
    void testIsDuplicateTrue() {
        when(userRepository.existsByBrid("BR123")).thenReturn(true);
        boolean result = userService.isDuplicate("BR123", "other@example.com");
        assertThat(result).isTrue();
    }

    @Test
    void testIsDuplicateFalse() {
        when(userRepository.existsByBrid("BR123")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        boolean result = userService.isDuplicate("BR123", "test@example.com");
        assertThat(result).isFalse();
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.saveUser(user)).isEqualTo(user);
    }

    @Test
    void testGetByBrid() {
        when(userRepository.findByBrid("BR123")).thenReturn(Optional.of(user));
        assertThat(userService.getByBrid("BR123")).isEqualTo(user);
    }

    @Test
    void testUpdateUserRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleService.getRoleByName("VIEWER")).thenReturn(Role.builder().roleName("VIEWER").build());
        userService.updateUserRole(1L, "VIEWER");
        verify(userRepository).save(argThat(updated -> updated.getRole().getRoleName().equals("VIEWER")));
    }

    @Test
    void testApproveRequest() {
        when(roleService.getRoleByName("EDITOR")).thenReturn(role);
        when(passwordEncoder.encode("plain")).thenReturn("encodedPass");
        when(userRepository.save(any())).thenReturn(user);
        User saved = userService.approveRequest(request, "EDITOR");
        assertThat(saved.getBrid()).isEqualTo("BR123");
        verify(userRequestService).save(argThat(r -> r.getStatus() == RequestStatus.APPROVED));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        assertThat(userService.getAllUsers()).containsExactly(user);
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThat(userService.getUserById(1L)).isPresent();
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        boolean deleted = userService.deleteUser(1L);
        assertThat(deleted).isTrue();
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserFail() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThat(userService.deleteUser(2L)).isFalse();
    }
    @Test
    void testInitializeDefaultUserWhenNoUsersExist() {
        when(userRepository.count()).thenReturn(0L);
        when(roleService.getRoleByName("SUPER_USER")).thenReturn(role);
        when(passwordEncoder.encode("admin123")).thenReturn("encodedAdmin");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.initializeDefaultUser();

        verify(userRepository).save(argThat(savedUser ->
                savedUser.getBrid().equals("admin01") &&
                        savedUser.getPassword().equals("encodedAdmin") &&
                        savedUser.getRole().equals(role)
        ));
    }

    @Test
    void testInitializeDefaultUserWhenUsersExist() {
        when(userRepository.count()).thenReturn(5L); // simulate existing users
        userService.initializeDefaultUser();
        verify(userRepository, never()).save(any());
    }

}
