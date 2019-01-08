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
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean future;
    private String dateOriginalValue;
    private String dateTimeOriginalValue;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    public String findAppointments(String requestBody){

        setStartDateTimeEndDateTime(requestBody);

        List<Appointment> appointments = repository.findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(startDateTime, endDateTime);

        if (!appointments.isEmpty()) {

            String baseString;

            future = !startDateTime.isBefore(LocalDateTime.now());
            String verb = future ? " heb" : " had";

            if (appointments.size() == 1) {
                baseString = dateOriginalValue + dateTimeOriginalValue + verb + " je de volgende afspraak: ";
            } else {
                baseString = dateOriginalValue + dateTimeOriginalValue + verb + " je de volgende afspraken: ";
            }

            return baseString + appointments.stream().
                    map(appointment -> appointment.toString()).
                    collect(Collectors.joining(", "));
        } else {
            return "Voor " + dateOriginalValue + dateTimeOriginalValue + " zijn er geen afspraken gevonden";
        }
    }

    private void setStartDateTimeEndDateTime(String requestBody) {

        final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);
        dateOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateOriginalValue");
        dateOriginalValue = cleanOriginalValue(dateOriginalValue);
        dateTimeOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTimeOriginalValue");
        dateTimeOriginalValue = cleanOriginalValue(dateTimeOriginalValue);

        final String date = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.date");
        final String dateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.date_time");
        final String startDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.startDateTime");
        final String endDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.endDateTime");

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
    }

    private String cleanOriginalValue(String value){
        if (value != null && value.length() > 0) {
            value = value.replaceAll("[^a-zA-Z0-9\\s]", "");
            value = value.substring(0, 1).toUpperCase() + value.substring(1);
        }
        return value;
    }
}
