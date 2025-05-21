package com.fdp.datareport.service;

import com.fdp.datareport.entity.Role;
import com.fdp.datareport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
    }

    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void initializeRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(Role.builder()
                    .roleName("SUPER_USER")
                    .canApproveUsers(true)
                    .canWrite(true)
                    .canDelete(true)
                    .build());

            roleRepository.save(Role.builder()
                    .roleName("EDITOR")
                    .canApproveUsers(false)
                    .canWrite(true)
                    .canDelete(true)
                    .build());

            roleRepository.save(Role.builder()
                    .roleName("VIEWER")
                    .canApproveUsers(false)
                    .canWrite(false)
                    .canDelete(false)
                    .build());
        }
    }
}

