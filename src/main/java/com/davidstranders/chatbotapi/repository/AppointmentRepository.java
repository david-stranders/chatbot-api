package com.davidstranders.chatbotapi.repository;

import com.davidstranders.chatbotapi.model.Appointment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends CrudRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a JOIN a.room r"
            + " WHERE ((a.start >= :start AND a.start <= :end)"
            + "    OR (a.start < :start AND a.end > :start))"
            + " AND (:roomNumber IS NULL OR r.number=:roomNumber)"
            + "ORDER BY start ASC")
    List<Appointment> findAppointments(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("roomNumber") Integer roomNumber);

}




