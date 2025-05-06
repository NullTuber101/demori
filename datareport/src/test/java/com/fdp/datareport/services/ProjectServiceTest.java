package com.fdp.datareport.services;

import com.fdp.datareport.entities.*;
import com.fdp.datareport.repositories.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private SprintRepository sprintRepository;

    @InjectMocks
    private ProjectService projectService;

    private AutoCloseable closeable;

    private Project project;
    private Area area;
    private Status status;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        area = new Area(1L, "area1", "lead1", "lead1@example.com");
        status = new Status(1L, "In Progress", 50);

        project = new Project(1L, "Project A", "Test Desc", "Dev1", "JIRA-123",
                area, status,
                new Date(System.currentTimeMillis() - 100000), // start
                new Date(System.currentTimeMillis()));         // end
    }

    @Test
    void testCreateProject() {
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.createProject(project);
        assertNotNull(result);
        assertEquals("Project A", result.getProjectName());
        verify(projectRepository).save(project);
    }

    @Test
    void testGetAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<Project> result = projectService.getAllProjects();
        assertEquals(1, result.size());
        assertEquals("area1", result.get(0).getArea().getName());
        assertEquals("In Progress", result.get(0).getStatus().getStatusName());
    }

    @Test
    void testGetProjectById_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        Optional<Project> result = projectService.getProjectById(1L);
        assertTrue(result.isPresent());
        assertEquals("Project A", result.get().getProjectName());
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.getProjectById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateProject_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Project updated = new Project(null, "Project B", "Updated Desc", "Dev2", "JIRA-456",
                area, status, project.getStartDate(), project.getEndDate());

        Optional<Project> result = projectService.updateProject(1L, updated);

        assertTrue(result.isPresent());
        assertEquals("Project B", result.get().getProjectName());
        assertEquals("Updated Desc", result.get().getDescription());
    }

    @Test
    void testUpdateProject_NotFound() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Project> result = projectService.updateProject(99L, project);
        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteProject_Found() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        boolean deleted = projectService.deleteProject(1L);
        assertTrue(deleted);

        verify(sprintRepository).deleteByProject(project);
        verify(projectRepository).deleteById(1L);
    }

    @Test
    void testDeleteProject_NotFound() {
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        boolean deleted = projectService.deleteProject(2L);
        assertFalse(deleted);
        verify(sprintRepository, never()).deleteByProject(any());
        verify(projectRepository, never()).deleteById(anyLong());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
