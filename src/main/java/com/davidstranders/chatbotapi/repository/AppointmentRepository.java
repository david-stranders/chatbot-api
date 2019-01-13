package com.davidstranders.chatbotapi.repository;

import com.davidstranders.chatbotapi.model.Appointment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    List<Appointment> findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(LocalDateTime start, LocalDateTime end);

}


