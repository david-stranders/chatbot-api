package com.davidstranders.chatbotapi.services;

import com.davidstranders.chatbotapi.model.Appointment;
import com.davidstranders.chatbotapi.model.Intent;
import com.davidstranders.chatbotapi.model.Person;
import com.davidstranders.chatbotapi.repository.AppointmentRepository;
import com.davidstranders.chatbotapi.repository.PersonRepository;
import com.davidstranders.chatbotapi.repository.RoomRepository;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import net.minidev.json.JSONArray;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    final Configuration conf = Configuration.defaultConfiguration().addOptions(Option.SUPPRESS_EXCEPTIONS);

    private final AppointmentRepository appointmentRepository;
    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String dateOriginalValue;
    private String dateTimeOriginalValue;
    private Intent intent;
    private List<String> requestedPersons;
    private List<Person> matchedPersons;
    private String room;
    private Integer roomNumber;

    List<Appointment> appointments;

    private boolean future;
    private boolean addRoomInfo = true;
    private boolean addPersonInfo = true;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PersonRepository personRepository,
                                  RoomRepository roomRepository) {
        this.appointmentRepository = appointmentRepository;
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
    }

    public String handleRequest(String requestBody) {
        matchIntent(requestBody);
        setStartAndEnd(requestBody);
        setOriginalDateTimeValues(requestBody);
        setOptionalQueryParams(requestBody);
        findAppointments();
        return buildResultString();
    }

    private void matchIntent(String requestBody) {

        String intentString = JsonPath.using(conf).parse(requestBody).read("$.queryResult.intent.displayName");
        intent = Intent.valueOf(intentString);

        if (intent.equals(Intent.Afspraken_welke)) {
            this.addRoomInfo = true;
            this.addPersonInfo = true;
        } else if (intent.equals(Intent.Afspraken_met_wie)) {
            this.addRoomInfo = false;
            this.addPersonInfo = true;
        } else if (intent.equals(Intent.Afspraken_waar)) {
            this.addRoomInfo = true;
            this.addPersonInfo = false;
        } else if (intent.equals(Intent.Afspraken_hoe_laat)) {
            this.addRoomInfo = false;
            this.addPersonInfo = false;
        } else if (intent.equals(Intent.Afspraken_hoeveel)) {
            this.addRoomInfo = false;
            this.addPersonInfo = false;
        }
    }

    private void findAppointments(){

        appointments = null;
        if (startDateTime == null || endDateTime == null){
            return;
        } else if (requestedPersons.isEmpty() && roomNumber == null) {
            appointments = appointmentRepository.findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(startDateTime, endDateTime);
        } else if (!requestedPersons.isEmpty() && roomNumber == null) {
            matchedPersons = personRepository.findAllByNameInIgnoreCase(requestedPersons);
            if (!matchedPersons.isEmpty()) {
                appointments = appointmentRepository.findAllByStartGreaterThanEqualAndStartLessThanEqualOrderByStartAsc(startDateTime, endDateTime);
                appointments = appointments.stream()
                        .filter(appointment -> CollectionUtils.containsAll(appointment.getPersons(), matchedPersons))
                        .collect(Collectors.toList());
            }
        } else if (requestedPersons.isEmpty() && roomNumber != null) {

        } else if (!requestedPersons.isEmpty() && roomNumber != null) {

        }
    }

    private String buildResultString(){
        if (startDateTime == null || endDateTime == null){
            return "Kan je je vraag herhalen met daarin een tijdsaanduiding? Bijvoorbeeld vandaag, vanmiddag, overmorgen, om 4 uur";
        }
        if (appointments != null && !appointments.isEmpty()) {
            String verb = future ? " heb je " : " had je ";
            StringBuilder resultMessage = new StringBuilder(dateOriginalValue + dateTimeOriginalValue + verb);
            resultMessage.append(appointments.size() + (appointments.size() > 1 ? " afspraken" : " afspraak"));

            if (matchedPersons != null && !matchedPersons.isEmpty()) {
                resultMessage
                        .append(" met ")
                        .append(matchedPersons.stream()
                                .map(person -> person.getName())
                                .collect(Collectors.joining(" en ")));
            }

            if (intent.equals(Intent.Afspraken_hoeveel)) {
                return resultMessage.toString();
            }

            return resultMessage
                    .append(": ")
                    .append(appointments.stream()
                            .map(appointment -> appointment.toString(addRoomInfo, addPersonInfo))
                            .collect(Collectors.joining(", ")))
                    .toString();
        } else {
            return "Voor " + dateOriginalValue + dateTimeOriginalValue + " zijn er geen afspraken gevonden" +
                    (matchedPersons != null && matchedPersons.isEmpty() ? " met " + requestedPersons.stream().collect(Collectors.joining(" en ")): "");
        }
    }

    private void setStartAndEnd(String requestBody) {

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

    private void setOptionalQueryParams(String requestBody) {
        JSONArray jsonArray = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.any");
        requestedPersons = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.size(); i++){
                requestedPersons.add((String)jsonArray.get(i));
            }
        }
        room = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.room");
        roomNumber = JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.number") instanceof Number ?
                 JsonPath.using(conf).parse(requestBody).read("$.queryResult.parameters.number") : null;
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
