package com.davidstranders.chatbotapi.services;

import com.davidstranders.chatbotapi.model.Appointment;
import com.davidstranders.chatbotapi.repository.AppointmentRepository;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    public String findAppointments(String requestBody){
        final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        String dateOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateOriginalValue");
        dateOriginalValue = dateOriginalValue.replaceAll("[^a-zA-Z]", "");
        String dateTimeOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTimeOriginalValue");
        dateTimeOriginalValue = dateTimeOriginalValue.replaceAll("[^a-zA-Z]", "");

        String date = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.date");
        String dateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.date-time.date_time");
        String startDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.startDateTime");
        String endDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.endDateTime");

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        DateTimeFormatter formatter;
        if (date != null && date.length() > 0){
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            startDateTime = LocalDateTime.parse(date.substring(0,10) + " 00:00", formatter);
            endDateTime = startDateTime.plusDays(1);
        }
        else if (dateTimeString != null && dateTimeString.length() > 0) {
            formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            startDateTime = LocalDateTime.parse(dateTimeString, formatter);
            endDateTime = LocalDateTime.parse(dateTimeString, formatter);
        } else {
            formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
            startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
            endDateTime = LocalDateTime.parse(endDateTimeString, formatter);
        }

        List<Appointment> appointments = repository.findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(startDateTime, endDateTime);

        if (!appointments.isEmpty()) {
            String baseString;
            if (appointments.size() == 1) {
                baseString = "Voor " + dateOriginalValue + dateTimeOriginalValue + " heb je de volgende afspraak: ";
            } else {
                baseString = "Voor " + dateOriginalValue + dateTimeOriginalValue + " heb je de volgende afspraken: ";
            }
            return baseString + appointments.toString().replace("[", "").replace("]", "");
        } else {
            return "Voor " + dateOriginalValue + dateTimeOriginalValue + " zijn er geen afspraken gevonden";
        }
    }
}
