package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScrumAreaServiceTest {

    @Mock
    private ScrumAreaRepository scrumAreaRepository;

    @InjectMocks
    private ScrumAreaService scrumAreaService;

    private ScrumArea sampleArea;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleArea = new ScrumArea(
                1L,
                "Sample Area",
                "Scrum Master",
                "Team A",
                "BOARD-123"
        );
    }

    @Test
    void testGetAllScrumAreas() {
        when(scrumAreaRepository.findAll()).thenReturn(List.of(sampleArea));

        List<ScrumArea> result = scrumAreaService.getAllScrumAreas();

        assertThat(result).hasSize(1).contains(sampleArea);
    }

    @Test
    void testGetScrumAreaById_whenFound() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));

        Optional<ScrumArea> result = scrumAreaService.getScrumAreaById(1L);

        assertThat(result).isPresent().contains(sampleArea);
    }

    @Test
    void testGetScrumAreaById_whenNotFound() {
        when(scrumAreaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ScrumArea> result = scrumAreaService.getScrumAreaById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    void testCreateScrumArea() {
        when(scrumAreaRepository.save(sampleArea)).thenReturn(sampleArea);

        ScrumArea created = scrumAreaService.createScrumArea(sampleArea);

        assertThat(created).isEqualTo(sampleArea);
    }

    @Test
    void testUpdateScrumArea_whenExists() {
        ScrumArea updated = new ScrumArea(
                1L,
                "Updated Area",
                "New Master",
                "Team B",
                "BOARD-456"
        );

        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));
        when(scrumAreaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ScrumArea result = scrumAreaService.updateScrumArea(1L, updated);

        assertThat(result.getAreaName()).isEqualTo("Updated Area");
        assertThat(result.getScrumTeam()).isEqualTo("Team B");
    }

    @Test
    void testUpdateScrumArea_whenNotFound() {
        when(scrumAreaRepository.findById(99L)).thenReturn(Optional.empty());

        ScrumArea input = new ScrumArea(99L, "X", "Y", "Z", "B-99");

        assertThatThrownBy(() -> scrumAreaService.updateScrumArea(99L, input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Scrum Area not found with id");
    }

    @Test
    void testDeleteScrumArea_whenExists() {
        when(scrumAreaRepository.findById(1L)).thenReturn(Optional.of(sampleArea));

        scrumAreaService.deleteScrumArea(1L);

        verify(scrumAreaRepository).delete(sampleArea);
    }

    @Test
    void testDeleteScrumArea_whenNotFound() {
        when(scrumAreaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scrumAreaService.deleteScrumArea(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Scrum Area not found with id");
    }
}
