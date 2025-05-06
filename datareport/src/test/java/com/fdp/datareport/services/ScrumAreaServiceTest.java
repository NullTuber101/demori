package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScrumAreaServiceTest {

    @InjectMocks
    private ScrumAreaService scrumAreaService;

    @Mock
    private ScrumAreaRepository scrumAreaRepository;

    private AutoCloseable closeable;

    private ScrumArea sampleArea;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        sampleArea = new ScrumArea(1L, "Platform", "Alice", "Team Rocket", "B1234");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetAllScrumAreas() {
        when(scrumAreaRepository.findAll()).thenReturn(List.of(sampleArea));

        List<ScrumArea> result = scrumAreaService.getAllScrumAreas();

        assertEquals(1, result.size());
        assertEquals("Platform", result.get(0).getAreaName());
    }

    @Test
    void testGetScrumAreaById_Found() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));

        Optional<ScrumArea> result = scrumAreaService.getScrumAreaById(1L);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getScrumMaster());
    }

    @Test
    void testGetScrumAreaById_NotFound() {
        when(scrumAreaRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<ScrumArea> result = scrumAreaService.getScrumAreaById(2L);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreateScrumArea() {
        when(scrumAreaRepository.save(any(ScrumArea.class))).thenReturn(sampleArea);

        ScrumArea result = scrumAreaService.createScrumArea(sampleArea);

        assertEquals("Team Rocket", result.getScrumTeam());
    }

    @Test
    void testUpdateScrumArea_Found() {
        ScrumArea updated = new ScrumArea(null, "Data", "Bob", "Team Alpha", "B5678");

        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));
        when(scrumAreaRepository.save(any(ScrumArea.class))).thenReturn(updated);

        ScrumArea result = scrumAreaService.updateScrumArea(1L, updated);

        assertEquals("Data", result.getAreaName());
        assertEquals("Bob", result.getScrumMaster());
    }

    @Test
    void testUpdateScrumArea_NotFound() {
        ScrumArea updated = new ScrumArea(null, "Data", "Bob", "Team Alpha", "B5678");

        when(scrumAreaRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                scrumAreaService.updateScrumArea(2L, updated));

        assertEquals("Scrum Area not found with id 2", ex.getMessage());
    }

    @Test
    void testDeleteScrumArea_Success() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));
        doNothing().when(scrumAreaRepository).delete(sampleArea);

        assertDoesNotThrow(() -> scrumAreaService.deleteScrumArea(1L));
        verify(scrumAreaRepository).delete(sampleArea);
    }

    @Test
    void testDeleteScrumArea_NotFound() {
        when(scrumAreaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                scrumAreaService.deleteScrumArea(99L));

        assertEquals("Scrum Area not found with id 99", ex.getMessage());
    }
}
