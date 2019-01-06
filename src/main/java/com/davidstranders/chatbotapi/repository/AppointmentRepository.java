package com.davidstranders.chatbotapi.repository;

import com.davidstranders.chatbotapi.model.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {
}
