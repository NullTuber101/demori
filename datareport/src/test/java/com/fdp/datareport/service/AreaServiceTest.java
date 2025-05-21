package com.fdp.datareport.service;

import com.fdp.datareport.entity.Area;
import com.fdp.datareport.repository.AreaRepository;
import com.fdp.datareport.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AreaServiceTest {

    @InjectMocks
    private AreaService areaService;

    @Mock
    private AreaRepository areaRepo;

    @Mock
    private ProjectRepository projectRepository;

    private Area sampleArea;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleArea = new Area(1L, "Test Area", "Lead Name", "lead@example.com");
    }

    @Test
    void testGetAllAreas() {
        when(areaRepo.findAll()).thenReturn(List.of(sampleArea));
        List<Area> result = areaService.getAllAreas();
        assertThat(result).hasSize(1).contains(sampleArea);
    }

    @Test
    void testGetAreaById_found() {
        when(areaRepo.findById(1L)).thenReturn(Optional.of(sampleArea));
        Optional<Area> result = areaService.getAreaById(1L);
        assertThat(result).isPresent().contains(sampleArea);
    }

    @Test
    void testGetAreaById_notFound() {
        when(areaRepo.findById(1L)).thenReturn(Optional.empty());
        Optional<Area> result = areaService.getAreaById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    void testAddArea() {
        when(areaRepo.save(sampleArea)).thenReturn(sampleArea);
        Area result = areaService.addArea(sampleArea);
        assertThat(result).isEqualTo(sampleArea);
    }

    @Test
    void testUpdateArea_exists() {
        when(areaRepo.existsById(1L)).thenReturn(true);
        when(areaRepo.save(any(Area.class))).thenReturn(sampleArea);

        Area updated = new Area(null, "Updated", "New Lead", "new@example.com");
        Area result = areaService.updateArea(1L, updated);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(areaRepo).save(updated);
    }

    @Test
    void testUpdateArea_notExists() {
        when(areaRepo.existsById(1L)).thenReturn(false);
        Area result = areaService.updateArea(1L, sampleArea);
        assertThat(result).isNull();
    }

    @Test
    void testDeleteArea_found() {
        when(areaRepo.findById(1L)).thenReturn(Optional.of(sampleArea));

        boolean deleted = areaService.deleteArea(1L);

        assertThat(deleted).isTrue();
        verify(projectRepository).deleteByArea(sampleArea);
        verify(areaRepo).deleteById(1L);
    }

    @Test
    void testDeleteArea_notFound() {
        when(areaRepo.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = areaService.deleteArea(1L);

        assertThat(deleted).isFalse();
        verify(projectRepository, never()).deleteByArea(any());
        verify(areaRepo, never()).deleteById(any());
    }
}
