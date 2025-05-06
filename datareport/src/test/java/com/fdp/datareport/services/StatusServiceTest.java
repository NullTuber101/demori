package com.fdp.datareport.services;

import com.fdp.datareport.entities.Project;
import com.fdp.datareport.entities.Status;
import com.fdp.datareport.repositories.ProjectRepository;
import com.fdp.datareport.repositories.StatusRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private StatusService statusService;

    private AutoCloseable closeable;

    private Status status1;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        status1 = new Status(1L, "In Progress", 50);
    }

    @Test
    void testGetAllStatuses() {
        when(statusRepository.findAll()).thenReturn(List.of(status1));

        List<Status> result = statusService.getAllStatuses();
        assertEquals(1, result.size());
        assertEquals("In Progress", result.get(0).getStatusName());
    }

    @Test
    void testGetStatusById_Found() {
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status1));

        Optional<Status> result = statusService.getStatusById(1L);
        assertTrue(result.isPresent());
        assertEquals(50, result.get().getPercentage());
    }

    @Test
    void testGetStatusById_NotFound() {
        when(statusRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Status> result = statusService.getStatusById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testCreateStatus() {
        when(statusRepository.save(status1)).thenReturn(status1);

        Status result = statusService.createStatus(status1);
        assertNotNull(result);
        assertEquals("In Progress", result.getStatusName());
    }

    @Test
    void testUpdateStatus_WhenExists() {
        when(statusRepository.existsById(1L)).thenReturn(true);
        when(statusRepository.save(any(Status.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Status updated = new Status(null, "Completed", 100);
        Status result = statusService.updateStatus(1L, updated);

        assertEquals(1L, result.getId());
        assertEquals("Completed", result.getStatusName());
        assertEquals(100, result.getPercentage());
    }

    @Test
    void testUpdateStatus_WhenNotExists() {
        when(statusRepository.existsById(1L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                statusService.updateStatus(1L, status1)
        );

        assertEquals("Status not found with id 1", ex.getMessage());
    }

    @Test
    void testDeleteStatus_WhenExists() {
        when(statusRepository.findById(1L)).thenReturn(Optional.of(status1));
        when(projectRepository.findByStatus(status1)).thenReturn(List.of());

        boolean deleted = statusService.deleteStatus(1L);

        assertTrue(deleted);
        verify(statusRepository).deleteById(1L);
    }

    @Test
    void testDeleteStatus_WhenUsedInProjects() {
        Project project1 = new Project();
        project1.setStatus(status1);

        when(statusRepository.findById(1L)).thenReturn(Optional.of(status1));
        when(projectRepository.findByStatus(status1)).thenReturn(List.of(project1));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean deleted = statusService.deleteStatus(1L);

        assertTrue(deleted);
        verify(projectRepository).save(project1);
        assertNull(project1.getStatus());
        verify(statusRepository).deleteById(1L);
    }

    @Test
    void testDeleteStatus_WhenNotFound() {
        when(statusRepository.findById(99L)).thenReturn(Optional.empty());

        boolean deleted = statusService.deleteStatus(99L);
        assertFalse(deleted);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
