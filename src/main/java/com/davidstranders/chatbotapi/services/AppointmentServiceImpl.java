package com.davidstranders.chatbotapi.services;

import com.davidstranders.chatbotapi.model.Appointment;
import com.davidstranders.chatbotapi.model.Intent;
import com.davidstranders.chatbotapi.repository.AppointmentRepository;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    private final AppointmentRepository repository;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String dateOriginalValue;
    private String dateTimeOriginalValue;
    private Intent intent;

    List<Appointment> appointments;

    private boolean future;
    private boolean addRoomInfo = true;
    private boolean addPersonInfo = true;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    public String matchIntent(String requestBody) {

        String intentString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.intent.displayName");
        intent = Intent.valueOf(intentString);

        if (intent.equals(Intent.Afspraken_welke)) {
            this.addRoomInfo = true;
            this.addPersonInfo = true;
            return findAppointmentsByDates(requestBody);
        } else if (intent.equals(Intent.Afspraken_met_wie)) {
            this.addRoomInfo = false;
            this.addPersonInfo = true;
            return findAppointmentsByDates(requestBody);
        } else if (intent.equals(Intent.Afspraken_waar)) {
            this.addRoomInfo = true;
            this.addPersonInfo = false;
            return findAppointmentsByDates(requestBody);
        } else if (intent.equals(Intent.Afspraken_hoe_laat)) {
            this.addRoomInfo = false;
            this.addPersonInfo = false;
            return findAppointmentsByDates(requestBody);
        } else if (intent.equals(Intent.Afspraken_hoeveel)) {
            this.addRoomInfo = false;
            this.addPersonInfo = false;
            return findAppointmentsByDates(requestBody);
        }

        return "Sorry, ik begrijp je niet. Kan je dit herhalen in andere bewoordingen?";
    }

    private String findAppointmentsByDates(String requestBody){

        setStartDateTimeEndDateTime(requestBody);
        setOriginalDateTimeValues(requestBody);

        if (startDateTime != null && endDateTime != null) {
            appointments = repository.findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(startDateTime, endDateTime);
        } else {
            return "Kan je je vraag herhalen met daarin een tijdsaanduiding? Bijvoorbeeld vandaag, vanmiddag, overmorgen, om 4 uur";
        }

        if (!appointments.isEmpty()) {

            String verb = future ? " heb je " : " had je ";
            String baseString = dateOriginalValue + dateTimeOriginalValue + verb;
            baseString = baseString + appointments.size() + (appointments.size() > 1 ? " afspraken" : " afspraak");

            if (intent.equals(Intent.Afspraken_hoeveel)) {
                return baseString;
            }

            return baseString + ": " + appointments.stream().
                        map(appointment -> appointment.toString(addRoomInfo, addPersonInfo)).
                        collect(Collectors.joining(", "));
        } else {
            return "Voor " + dateOriginalValue + dateTimeOriginalValue + " zijn er geen afspraken gevonden";
        }
    }

    private void setStartDateTimeEndDateTime(String requestBody) {

        startDateTime = null;
        endDateTime = null;

        final String dateString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.date");
        final String dateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.date_time");
        final String startDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.startDateTime");
        final String endDateTimeString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTime.endDateTime");

        if (dateString != null && dateString.length() > 0){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateString.substring(0,10), formatter);
            future = !date.isBefore(LocalDate.now());
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            startDateTime = LocalDateTime.parse(dateString.substring(0,10) + " 00:00", formatter);
            endDateTime = startDateTime.plusDays(1);
        }
        else if (dateTimeString != null && dateTimeString.length() > 0) {
            setFutureAndDates(dateTimeString, dateTimeString);
        }
        else if (startDateTimeString != null && startDateTimeString.length() > 0 &&
                endDateTimeString != null && endDateTimeString.length() > 0) {
            setFutureAndDates(startDateTimeString, endDateTimeString);
        }
    }

    private void setFutureAndDates(String startDateTimeString, String endDateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        future = !startDateTime.isBefore(LocalDateTime.now());
        endDateTime = LocalDateTime.parse(endDateTimeString, formatter);
    }

    private void setOriginalDateTimeValues(String requestBody) {
        dateOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateOriginalValue");
        dateOriginalValue = cleanOriginalValue(dateOriginalValue);
        dateTimeOriginalValue = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.dateTimeOriginalValue");
        dateTimeOriginalValue = cleanOriginalValue(dateTimeOriginalValue);
    }

    private String cleanOriginalValue(String value){
        if (value != null && value.length() > 0) {
            value = value.replaceAll("[^a-zA-Z0-9\\s]", "");
            value = value.substring(0, 1).toUpperCase() + value.substring(1);
        }
        return value;
    }
}
