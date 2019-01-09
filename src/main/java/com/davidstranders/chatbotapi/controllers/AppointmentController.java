package com.davidstranders.chatbotapi.controllers;

import com.davidstranders.chatbotapi.model.*;
import com.davidstranders.chatbotapi.services.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("api/appointment")
public class AppointmentController {

    private final AppointmentService service;

    @Autowired
    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<DialogFlowResponseEntity> findAppointments(@RequestBody final String requestJson) {
        String result = service.matchIntent(requestJson);
        DialogFlowResponseEntity responseEntity = new DialogFlowResponseEntity(result, "findAppointments");
        return new ResponseEntity<>(responseEntity, HttpStatus.OK);
    }


}
