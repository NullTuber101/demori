package com.fdp.datareport.controller;

import com.fdp.datareport.entity.Status;
import com.fdp.datareport.service.StatusService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatusService statusService;

    private String editorToken;
    private String viewerToken;

    private Status sampleStatus;

    @BeforeEach
    void setUp() {
        sampleStatus = new Status();
        sampleStatus.setId(1L);
        sampleStatus.setStatusName("In Progress");
        sampleStatus.setPercentage(50);
    }

}
