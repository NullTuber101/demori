package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.entities.Velocity;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import com.fdp.datareport.repositories.VelocityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VelocityServiceTest {

    @Mock
    private VelocityRepository velocityRepository;

    @Mock
    private ScrumAreaRepository scrumAreaRepository;

    @InjectMocks
    private VelocityService velocityService;

    private ScrumArea scrumArea;
    private Velocity velocity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        scrumArea = new ScrumArea(1L, "Area 1", "Scrum Master", "Team", "Board-1");
        velocity = new Velocity(1L, "Sprint 1", 25.0f, LocalDate.now(), scrumArea);
    }

    @Test
    void testGetAllVelocities() {
        when(velocityRepository.findAll()).thenReturn(List.of(velocity));
        List<Velocity> result = velocityService.getAllVelocities();
        assertThat(result).hasSize(1);
    }

    @Test
    void testGetVelocitiesByScrumAreaId() {
        when(velocityRepository.findByScrumAreaId(1L)).thenReturn(List.of(velocity));
        List<Velocity> result = velocityService.getVelocitiesByScrumAreaId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    void testCreateVelocity() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(scrumArea));
        when(velocityRepository.save(any())).thenReturn(velocity);

        Velocity newVelocity = new Velocity(null, "Sprint 2", 30.0f, LocalDate.now(), null);
        Velocity result = velocityService.createVelocity(1L, newVelocity);

        assertThat(result).isNotNull();
        verify(velocityRepository).save(any(Velocity.class));
    }

    @Test
    void testUpdateVelocity() {
        Velocity updated = new Velocity(null, "Sprint Updated", 40.0f, LocalDate.now(), scrumArea);
        when(velocityRepository.findById(1L)).thenReturn(Optional.of(velocity));
        when(velocityRepository.save(any())).thenReturn(updated);

        Velocity result = velocityService.updateVelocity(1L, updated);
        assertThat(result.getSprintName()).isEqualTo("Sprint Updated");
    }

    @Test
    void testDeleteVelocity() {
        velocityService.deleteVelocity(1L);
        verify(velocityRepository).deleteById(1L);
    }

    @Test
    void testGetVelocityGroupedByScrumArea() {
        when(scrumAreaRepository.findAll()).thenReturn(List.of(scrumArea));
        when(velocityRepository.findTop3ByScrumAreaOrderBySprintEndDateDesc(scrumArea)).thenReturn(List.of(velocity));

        List<Map<String, Object>> result = velocityService.getVelocityGroupedByScrumArea();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).containsKeys("scrumAreaName", "velocities");
    }
}
