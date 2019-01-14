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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;

@Component
public class DataLoader {

    private final AppointmentRepository appointmentRepository;
    private final PersonRepository personRepository;
    private final RoomRepository roomRepository;

    private ArrayList<Room> rooms;
    private ArrayList<Person> persons;

    private int roomCounter = 0;
    private int personCounter = 0;
    private LocalDate localDate = LocalDate.now().minusDays(10);
    private int morningHours = 9;
    private int afternoonHours = 12;
    private int minutes = 00;

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

        Room room1 = new Room("kamer 1");
        room1 = roomRepository.save(room1);
        Room room2 = new Room("kamer 2");
        room2 = roomRepository.save(room2);
        Room room3 = new Room("kamer 3");
        room3 = roomRepository.save(room3);
        Room room4 = new Room("kamer 4");
        room4 = roomRepository.save(room4);
        Room room5 = new Room("kamer 5");
        room5 = roomRepository.save(room5);

        this.rooms = new ArrayList<>(Arrays.asList(room1, room2, room3, room4, room5));

        Person jan = new Person("Jan Jansen");
        jan = personRepository.save(jan);
        Person piet = new Person("Piet Pietersen");
        piet = personRepository.save(piet);
        Person klaas = new Person("Klaas Klaassen");
        klaas = personRepository.save(klaas);
        Person david = new Person("Peter Petersen");
        david = personRepository.save(david);
        Person kees = new Person("Kees de Vries");
        kees = personRepository.save(kees);
        Person karel = new Person("Karel de Jong");
        karel = personRepository.save(karel);

        this.persons = new ArrayList<>(Arrays.asList(jan, piet, klaas, david, kees, karel));

        this.createAppointments(60);
    }

    private void createAppointments(int numberOfDays) {

        for (int x = 0; x < (numberOfDays + 10); x++) {
            for (int y = 0; y < 2; y++) {

                LocalTime morningStartTime = LocalTime.of(morningHours, minutes);
                LocalTime morningEndTime = morningStartTime.plusHours(1);
                ArrayList morningAttendees = new ArrayList<>();
                morningAttendees.add(persons.get(personCounter));
                adjustPersonCounter();
                morningAttendees.add(persons.get(personCounter));
                adjustPersonCounter();

                Appointment morningAppointment = new Appointment(LocalDateTime.of(localDate, morningStartTime),
                        LocalDateTime.of(localDate, morningEndTime),
                        rooms.get(roomCounter), morningAttendees);
                appointmentRepository.save(morningAppointment);

                adjustMinutes();
                adjustRoomCounter();

                LocalTime afternoonStartTime = LocalTime.of(afternoonHours, minutes);
                LocalTime afternoonEndTime = afternoonStartTime.plusHours(1);
                ArrayList afternoonAttendees = new ArrayList<>();
                afternoonAttendees.add(persons.get(personCounter));
                adjustPersonCounter();
                afternoonAttendees.add(persons.get(personCounter));
                adjustPersonCounter();

                Appointment afternoonAppointment = new Appointment(LocalDateTime.of(localDate, afternoonStartTime),
                        LocalDateTime.of(localDate, afternoonEndTime),
                        rooms.get(roomCounter), afternoonAttendees);
                appointmentRepository.save(afternoonAppointment);

                adjustAll();
            }

            localDate = localDate.plusDays(1);
        }
    }

    private void adjustRoomCounter(){
        if (roomCounter < (rooms.size() - 1)) {
            roomCounter++;
        } else {
            roomCounter = 0;
        }
    }

    private void adjustPersonCounter(){
        if (personCounter < (persons.size() - 1)) {
            personCounter++;
        } else {
            personCounter = 0;
        }
    }

    private void adjustMorningHours(){
        if (morningHours < 12) {
            morningHours++;
        } else {
            morningHours = 9;
        }
    }

    private void adjustAfternoonHours(){
        if (afternoonHours < 18) {
            afternoonHours++;
        } else {
            afternoonHours = 12;
        }
    }

    private void adjustMinutes(){
        if (minutes < 45) {
            minutes = minutes + 15;
        } else {
            minutes = 00;
        }
    }


    private void adjustAll() {
        adjustPersonCounter();;
        adjustRoomCounter();;
        adjustMorningHours();
        adjustAfternoonHours();
        adjustMinutes();
    }

}
