package com.fdp.datareport.services;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Sprint;
import com.fdp.datareport.entities.Status;
import com.fdp.datareport.repositories.ProjectRepository;
import com.fdp.datareport.repositories.SprintRepository;
import com.fdp.datareport.repositories.StatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class SprintServiceTest {

    @InjectMocks
    private SprintService sprintService;

    @Mock
    private SprintRepository sprintRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StatusRepository statusRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Sprint createMockSprint() {
        Sprint sprint = new Sprint();
        sprint.setId(1L);
        sprint.setSprintName("Sprint 1");
        sprint.setSprintStartDate(new Date());
        sprint.setSprintEndDate(new Date());
        sprint.setSprintJira("SPR-001");
        sprint.setSprintDescription("Test Sprint");
        sprint.setAssignedTo("John Doe");

        Status status = new Status();
        status.setId(10L);
        sprint.setSprintFor(status);

        return sprint;
    }

    @Test
    void testCreateSprint() {
        Sprint sprint = createMockSprint();
        Project project = new Project();
        project.setId(100L);
        Status status = new Status();
        status.setId(10L);

        when(projectRepository.findById(100L)).thenReturn(Optional.of(project));
        when(statusRepository.findById(10L)).thenReturn(Optional.of(status));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        Sprint created = sprintService.createSprint(100L, sprint);

        assertThat(created).isNotNull();
        assertThat(created.getSprintName()).isEqualTo("Sprint 1");
    }

    @Test
    void testGetSprintsByProject() {
        Sprint sprint = createMockSprint();
        Project project = new Project();
        project.setId(100L);
        sprint.setProject(project);

        when(sprintRepository.findAll()).thenReturn(List.of(sprint));

        List<Sprint> result = sprintService.getSprintsByProject(100L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSprintName()).isEqualTo("Sprint 1");
    }

    @Test
    void testGetSprintById() {
        Sprint sprint = createMockSprint();
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));

        Sprint result = sprintService.getSprintById(1L);
        assertThat(result.getAssignedTo()).isEqualTo("John Doe");
    }

    @Test
    void testUpdateSprint() {
        Sprint oldSprint = createMockSprint();
        Sprint updated = createMockSprint();
        updated.setSprintName("Updated Sprint");

        Status status = new Status();
        status.setId(10L);

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(oldSprint));
        when(statusRepository.findById(10L)).thenReturn(Optional.of(status));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(updated);

        Sprint result = sprintService.updateSprint(1L, updated);
        assertThat(result.getSprintName()).isEqualTo("Updated Sprint");
    }

    @Test
    void testDeleteSprint() {
        when(sprintRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sprintRepository).deleteById(1L);

        sprintService.deleteSprint(1L);
        verify(sprintRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNonexistentSprint() {
        when(sprintRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> sprintService.deleteSprint(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Sprint not found");
    }
}
