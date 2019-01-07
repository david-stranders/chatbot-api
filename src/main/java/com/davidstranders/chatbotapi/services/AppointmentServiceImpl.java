package com.davidstranders.chatbotapi.services;

import com.davidstranders.chatbotapi.model.Appointment;
import com.davidstranders.chatbotapi.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    public String findAppointments(String requestBody){

        List<Appointment> appointments = repository.findAllByOrderByStartDesc();

        return appointments.toString();
    }
}
