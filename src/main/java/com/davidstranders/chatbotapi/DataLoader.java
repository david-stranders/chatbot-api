package com.davidstranders.chatbotapi;

import com.davidstranders.chatbotapi.model.Appointment;
import com.davidstranders.chatbotapi.model.Person;
import com.davidstranders.chatbotapi.model.Room;
import com.davidstranders.chatbotapi.repository.AppointmentRepository;
import com.davidstranders.chatbotapi.repository.PersonRepository;
import com.davidstranders.chatbotapi.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class DataLoader {

    private final AppointmentRepository appointmentRepository;
    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public DataLoader(AppointmentRepository appointmentRepository,
                      PersonRepository personRepository,
                      RoomRepository roomRepository) {
        this.appointmentRepository = appointmentRepository;
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    private void loadData() {

        Room room1 = new Room("Kamer 1");
        room1 = roomRepository.save(room1);
        Room room2 = new Room("Kamer 2");
        room2 = roomRepository.save(room2);
        Room room3 = new Room("Kamer 3");
        room3 = roomRepository.save(room3);

        Person jan = new Person("Jan Jansen");
        jan = personRepository.save(jan);
        Person piet = new Person( "Piet Pietersen");
        piet = personRepository.save(piet);
        Person klaas = new Person("Klaas Klaassen");
        klaas = personRepository.save(klaas);
        Person david = new Person("David Stranders");
        david = personRepository.save(david);

        Appointment appointment1 = new Appointment(LocalDateTime.of(2019, 01, 8, 14, 00),
                LocalDateTime.of(2019, 01, 10, 15, 00),
                room1, new ArrayList<>(Arrays.asList(david, jan)));
        appointmentRepository.save(appointment1);

        Appointment appointment2 = new Appointment(LocalDateTime.of(2019, 01, 8, 16, 00),
                LocalDateTime.of(2019, 01, 10, 17, 30),
                room1, new ArrayList<>(Arrays.asList(david, jan)));
        appointmentRepository.save(appointment2);

        Appointment appointment3 = new Appointment(LocalDateTime.of(2019, 01, 9, 9, 00),
                LocalDateTime.of(2019, 01, 11, 12, 30),
                room2, new ArrayList<>(Arrays.asList(david, jan, piet)));
        appointmentRepository.save(appointment3);

        Appointment appointment4 = new Appointment(LocalDateTime.of(2019, 01, 9, 13, 00),
                LocalDateTime.of(2019, 01, 11, 14, 30),
                room3, new ArrayList<>(Arrays.asList(david, klaas)));
        appointmentRepository.save(appointment4);

    }
}
