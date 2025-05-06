package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScrumAreaService {

    @Autowired
    private ScrumAreaRepository scrumAreaRepository;

    public List<ScrumArea> getAllScrumAreas() {
        return scrumAreaRepository.findAll();
    }

    public Optional<ScrumArea> getScrumAreaById(Long id) {
        return scrumAreaRepository.findById(id);
    }

    public ScrumArea createScrumArea(ScrumArea scrumArea) {
        return scrumAreaRepository.save(scrumArea);
    }

    public ScrumArea updateScrumArea(Long id, ScrumArea updatedScrumArea) {
        return scrumAreaRepository.findById(id).map(area -> {
            area.setAreaName(updatedScrumArea.getAreaName());
            area.setScrumMaster(updatedScrumArea.getScrumMaster());
            area.setScrumTeam(updatedScrumArea.getScrumTeam());
            area.setBoardId(updatedScrumArea.getBoardId());
            return scrumAreaRepository.save(area);
        }).orElseThrow(() -> new RuntimeException("Scrum Area not found with id " + id));
    }

    public void deleteScrumArea(Long id) {
        ScrumArea area = scrumAreaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scrum Area not found with id " + id));
        scrumAreaRepository.delete(area); // Automatically deletes velocities due to cascade
    }
}
