package com.fdp.datareport.services;

import com.fdp.datareport.entities.Area;
import com.fdp.datareport.repositories.AreaRepository;
import com.fdp.datareport.repositories.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AreaServiceTest {

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private AreaService areaService;

    private AutoCloseable closeable;

    private Area area1;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        area1 = new Area(1L, "area1", "lead1", "lead1@example.com");
    }

    @Test
    void testGetAllAreas() {
        List<Area> areas = List.of(area1);
        when(areaRepository.findAll()).thenReturn(areas);

        List<Area> result = areaService.getAllAreas();
        assertEquals(1, result.size());
        assertEquals("area1", result.get(0).getName());
        verify(areaRepository, times(1)).findAll();
    }

    @Test
    void testGetAreaById_Found() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area1));

        Optional<Area> result = areaService.getAreaById(1L);
        assertTrue(result.isPresent());
        assertEquals("area1", result.get().getName());
    }

    @Test
    void testGetAreaById_NotFound() {
        when(areaRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Area> result = areaService.getAreaById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testAddArea() {
        when(areaRepository.save(area1)).thenReturn(area1);

        Area result = areaService.addArea(area1);
        assertNotNull(result);
        assertEquals("lead1", result.getLeadName());
        verify(areaRepository, times(1)).save(area1);
    }

    @Test
    void testUpdateArea_WhenExists() {
        when(areaRepository.existsById(1L)).thenReturn(true);
        when(areaRepository.save(any(Area.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // <-- Fix here

        Area updatedArea = new Area(null, "area1-updated", "lead1-updated", "lead1-updated@example.com");
        Area result = areaService.updateArea(1L, updatedArea);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("area1-updated", result.getName());
    }
    @Test
    void testUpdateArea_WhenNotExists() {
        when(areaRepository.existsById(1L)).thenReturn(false);

        Area result = areaService.updateArea(1L, area1);
        assertNull(result);
    }

    @Test
    void testDeleteArea_WhenExists() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(area1));
        doNothing().when(projectRepository).deleteByArea(area1);
        doNothing().when(areaRepository).deleteById(1L);

        boolean deleted = areaService.deleteArea(1L);

        assertTrue(deleted);
        verify(projectRepository).deleteByArea(area1);
        verify(areaRepository).deleteById(1L);
    }

    @Test
    void testDeleteArea_WhenNotFound() {
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = areaService.deleteArea(1L);
        assertFalse(deleted);
        verify(areaRepository, never()).deleteById(anyLong());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}
