package com.fdp.datareport.services;

import com.fdp.datareport.entities.ScrumArea;
import com.fdp.datareport.entities.Velocity;
import com.fdp.datareport.repositories.ScrumAreaRepository;
import com.fdp.datareport.repositories.VelocityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VelocityService {

    @Autowired
    private VelocityRepository velocityRepository;

    @Autowired
    private ScrumAreaRepository scrumAreaRepository;

    public List<Velocity> getAllVelocities() {
        return velocityRepository.findAll();
    }

    public List<Velocity> getVelocitiesByScrumAreaId(Long areaId) {
        return velocityRepository.findByScrumAreaId(areaId);
    }

    public Velocity createVelocity(Long areaId, Velocity velocity) {
        ScrumArea area = scrumAreaRepository.findById(areaId)
                .orElseThrow(() -> new RuntimeException("Scrum Area not found"));
        velocity.setScrumArea(area);
        return velocityRepository.save(velocity);
    }

    public Velocity updateVelocity(Long id, Velocity updatedVelocity) {
        return velocityRepository.findById(id)
                .map(existing -> {
                    existing.setSprintName(updatedVelocity.getSprintName());
                    existing.setVelocity(updatedVelocity.getVelocity());
                    existing.setSprintEndDate(updatedVelocity.getSprintEndDate());
                    return velocityRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Velocity not found with id " + id));
    }

    public void deleteVelocity(Long id) {
        velocityRepository.deleteById(id);
    }

    // ──────── NEW METHOD FOR CHART DATA ────────
    public List<Map<String, Object>> getVelocityGroupedByScrumArea() {
        List<ScrumArea> scrumAreas = scrumAreaRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (ScrumArea area : scrumAreas) {
            List<Velocity> latestVelocities = velocityRepository.findTop3ByScrumAreaOrderBySprintEndDateDesc(area);

            List<Map<String, Object>> velocityList = latestVelocities.stream().map(v -> {
                Map<String, Object> vMap = new HashMap<>();
                vMap.put("sprintName", v.getSprintName());
                vMap.put("velocity", v.getVelocity());
                vMap.put("sprintEndDate", v.getSprintEndDate());
                return vMap;
            }).collect(Collectors.toList());

            Map<String, Object> areaMap = new HashMap<>();
            areaMap.put("scrumAreaName", area.getAreaName());
            areaMap.put("velocities", velocityList);

            result.add(areaMap);
        }

        return result;
    }
}
