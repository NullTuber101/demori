package com.fdp.datareport.services;

import com.fdp.datareport.entities.*;
import com.fdp.datareport.repositories.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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

    private AutoCloseable closeable;

    private Project project;
    private Status status;
    private Sprint sprint;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        project = new Project(1L, "Project X", "desc", "Dev1", "JIRA-001", null, null,
                new Date(), new Date());

        status = new Status(1L, "In Progress", 50);

        sprint = new Sprint(1L, "Sprint 1", new Date(), new Date(), "JIRA-456",
                "Sprint desc", "John Doe", status, project);
    }

    @Test
    void testCreateSprint_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(statusRepository.findById(status.getId())).thenReturn(Optional.of(status));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);

        Sprint result = sprintService.createSprint(1L, sprint);

        assertNotNull(result);
        assertEquals("Sprint 1", result.getSprintName());
        verify(sprintRepository).save(sprint);
    }

    @Test
    void testCreateSprint_ProjectNotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sprintService.createSprint(99L, sprint));

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testGetSprintsByProject() {
        Sprint sprint2 = new Sprint(2L, "Sprint 2", new Date(), new Date(), "JIRA-789", "desc2", "Jane", status, project);
        when(sprintRepository.findAll()).thenReturn(List.of(sprint, sprint2));

        List<Sprint> result = sprintService.getSprintsByProject(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetSprintById_Success() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));

        Sprint result = sprintService.getSprintById(1L);
        assertEquals("Sprint 1", result.getSprintName());
    }

    @Test
    void testGetSprintById_NotFound() {
        when(sprintRepository.findById(42L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                sprintService.getSprintById(42L));

        assertEquals("Sprint not found", exception.getMessage());
    }

    @Test
    void testUpdateSprint_Success() {
        Sprint updated = new Sprint(null, "Sprint 1 Updated", new Date(), new Date(), "JIRA-999",
                "Updated desc", "Jane Smith", status, project);

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        when(statusRepository.findById(status.getId())).thenReturn(Optional.of(status));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(updated);

        Sprint result = sprintService.updateSprint(1L, updated);

        assertEquals("Sprint 1 Updated", result.getSprintName());
        assertEquals("Jane Smith", result.getAssignedTo());
    }

    @Test
    void testUpdateSprint_SprintNotFound() {
        when(sprintRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                sprintService.updateSprint(404L, sprint));

        assertEquals("Sprint not found", ex.getMessage());
    }

    @Test
    void testDeleteSprint_Success() {
        when(sprintRepository.existsById(1L)).thenReturn(true);

        sprintService.deleteSprint(1L);
        verify(sprintRepository).deleteById(1L);
    }

    @Test
    void testDeleteSprint_NotFound() {
        when(sprintRepository.existsById(999L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                sprintService.deleteSprint(999L));

        assertEquals("Sprint not found", ex.getMessage());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
