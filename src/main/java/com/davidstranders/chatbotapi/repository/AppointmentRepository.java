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
//            + " WHERE r.n = "
            + " WHERE ((a.start >= :start AND a.end <= :end)"
            + "    OR (a.start < :start AND a.end > :end))"
            + " AND (:room IS NULL OR r.name=:room)"
            + "ORDER BY start ASC")
    List<Appointment> findAppointments(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end,
                                       @Param("room") String room);

}


//    @Query("SELECT h"
//            + " FROM  Hond h"
//            + " WHERE ("
//            + "   h.naam LIKE :naam"
//            + "   OR EXISTS (FROM h.stamboekEntries s WHERE s.stamboeknummer LIKE :stamboeknummer)"
//            + "   OR EXISTS (FROM h.chips c WHERE c.chipnummer LIKE :chipnummer)"
//            + " )"
//            + " AND (:isNLHond IS NULL OR h.nederlandseHond = :isNLHond)"
//            + " AND (:isLevend IS NULL OR ((:isLevend IS TRUE AND h.datumOverlijden IS NULL) OR (:isLevend IS FALSE AND h.datumOverlijden IS NOT NULL)))"
//            + " AND h.geslacht = :geslacht"
//    )
//    Page<Hond> findHond(@Param("naam") String naam,
//                        @Param("stamboeknummer") String stamboeknummer,
//                        @Param("chipnummer") String chipnummer,
//                        @Param("geslacht") Geslacht geslacht,
//                        @Param("isNLHond") Boolean isNLHond,
//                        @Param("isLevend") Boolean isLevend,
//                        Pageable pageable);



