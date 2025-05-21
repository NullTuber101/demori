package com.fdp.datareport.service;

import com.fdp.datareport.entity.Project;
import com.fdp.datareport.entity.Status;
import com.fdp.datareport.repository.ProjectRepository;
import com.fdp.datareport.repository.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatusServiceTest {

    @InjectMocks
    private StatusService statusService;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Status createStatus() {
        return new Status(1L, "IN PROGRESS", 50);
    }

    @Test
    void testGetAllStatuses() {
        List<Status> statuses = List.of(createStatus());
        when(statusRepository.findAll()).thenReturn(statuses);

        List<Status> result = statusService.getAllStatuses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusName()).isEqualTo("IN PROGRESS");
    }

    @Test
    void testGetStatusById() {
        Status status = createStatus();
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));

        Optional<Status> result = statusService.getStatusById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPercentage()).isEqualTo(50);
    }

    @Test
    void testCreateStatus() {
        Status status = createStatus();
        when(statusRepository.save(status)).thenReturn(status);

        Status result = statusService.createStatus(status);

        assertThat(result.getStatusName()).isEqualTo("IN PROGRESS");
    }

    @Test
    void testUpdateStatusWhenExists() {
        Status updated = createStatus();
        updated.setStatusName("DONE");

        when(statusRepository.existsById(1L)).thenReturn(true);
        when(statusRepository.save(updated)).thenReturn(updated);

        Status result = statusService.updateStatus(1L, updated);

        assertThat(result.getStatusName()).isEqualTo("DONE");
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void testUpdateStatusWhenNotExists() {
        Status updated = createStatus();
        when(statusRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> statusService.updateStatus(1L, updated))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Status not found");
    }

    @Test
    void testDeleteStatusWhenExists() {
        Status status = createStatus();
        List<Project> projects = List.of(
                new Project(1L, "Proj1", "Desc", "Dev", "JIRA-1", null, status, new Date(), new Date())
        );

        when(statusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(projectRepository.findByStatus(status)).thenReturn(projects);

        boolean deleted = statusService.deleteStatus(1L);

        assertThat(deleted).isTrue();
        verify(projectRepository).save(any(Project.class));
        verify(statusRepository).deleteById(1L);
    }

    @Test
    void testDeleteStatusWhenNotExists() {
        when(statusRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = statusService.deleteStatus(1L);

        assertThat(result).isFalse();
        verify(statusRepository, never()).deleteById(anyLong());
    }
}
