package com.fdp.datareport.services;

import com.fdp.datareport.entities.Role;
import com.fdp.datareport.entities.User;
import com.fdp.datareport.entities.UserRequest;
import com.fdp.datareport.enums.RequestStatus;
import com.fdp.datareport.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRequestService userRequestService;
    private final PasswordEncoder passwordEncoder;

    public boolean isDuplicate(String brid, String email) {
        return userRepository.existsByBrid(brid) || userRepository.existsByEmail(email);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getByBrid(String brid) {
        return userRepository.findByBrid(brid).orElse(null);
    }

    public void updateUserRole(Long id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleService.getRoleByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }


    public User approveRequest(UserRequest request, String roleName) {
        Role role = roleService.getRoleByName(roleName);

        User user = User.builder()
                .brid(request.getBrid())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(role)
                .build();

        User saved = userRepository.save(user);

        request.setStatus(RequestStatus.APPROVED);
        userRequestService.save(request); // update status in request table
        return saved;
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }
}
