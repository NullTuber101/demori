package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.entities.Velocity;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import com.fdp.datareport.repositories.VelocityRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VelocityServiceTest {

    @InjectMocks
    private VelocityService velocityService;

    @Mock
    private VelocityRepository velocityRepository;

    @Mock
    private ScrumAreaRepository scrumAreaRepository;

    private AutoCloseable closeable;

    private ScrumArea scrumArea;
    private Velocity velocity;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        scrumArea = new ScrumArea(1L, "Data", "John", "Team-X", "BR-9");
        velocity = new Velocity(1L, "Sprint 1", 25.5f, LocalDate.of(2024, 6, 1), scrumArea);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetAllVelocities() {
        when(velocityRepository.findAll()).thenReturn(List.of(velocity));

        List<Velocity> result = velocityService.getAllVelocities();

        assertEquals(1, result.size());
        assertEquals("Sprint 1", result.get(0).getSprintName());
    }

    @Test
    void testGetVelocitiesByScrumAreaId() {
        when(velocityRepository.findByScrumAreaId(1L)).thenReturn(List.of(velocity));

        List<Velocity> result = velocityService.getVelocitiesByScrumAreaId(1L);

        assertEquals(1, result.size());
        assertEquals(25.5f, result.get(0).getVelocity());
    }

    @Test
    void testCreateVelocity() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(scrumArea));
        when(velocityRepository.save(any(Velocity.class))).thenReturn(velocity);

        Velocity result = velocityService.createVelocity(1L, velocity);

        assertNotNull(result);
        assertEquals("Sprint 1", result.getSprintName());
    }

    @Test
    void testCreateVelocity_AreaNotFound() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                velocityService.createVelocity(1L, velocity));

        assertEquals("Scrum Area not found", ex.getMessage());
    }

    @Test
    void testUpdateVelocity_Found() {
        Velocity updated = new Velocity(null, "Sprint 1 Updated", 30.0f, LocalDate.of(2024, 6, 15), scrumArea);

        when(velocityRepository.findById(1L)).thenReturn(Optional.of(velocity));
        when(velocityRepository.save(any(Velocity.class))).thenReturn(updated);

        Velocity result = velocityService.updateVelocity(1L, updated);

        assertEquals("Sprint 1 Updated", result.getSprintName());
        assertEquals(30.0f, result.getVelocity());
    }

    @Test
    void testUpdateVelocity_NotFound() {
        when(velocityRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                velocityService.updateVelocity(1L, velocity));

        assertEquals("Velocity not found with id 1", ex.getMessage());
    }

    @Test
    void testDeleteVelocity() {
        doNothing().when(velocityRepository).deleteById(1L);

        assertDoesNotThrow(() -> velocityService.deleteVelocity(1L));
        verify(velocityRepository).deleteById(1L);
    }

    @Test
    void testGetVelocityGroupedByScrumArea() {
        Velocity v1 = new Velocity(1L, "Sprint 2", 20.0f, LocalDate.of(2024, 6, 2), scrumArea);
        Velocity v2 = new Velocity(2L, "Sprint 1", 18.0f, LocalDate.of(2024, 5, 26), scrumArea);

        when(scrumAreaRepository.findAll()).thenReturn(List.of(scrumArea));
        when(velocityRepository.findTop3ByScrumAreaOrderBySprintEndDateDesc(scrumArea)).thenReturn(List.of(v1, v2));

        List<Map<String, Object>> result = velocityService.getVelocityGroupedByScrumArea();

        assertEquals(1, result.size());
        assertEquals("Data", result.get(0).get("scrumAreaName"));

        List<?> velocities = (List<?>) result.get(0).get("velocities");
        assertEquals(2, velocities.size());
    }
}
