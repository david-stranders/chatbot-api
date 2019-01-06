package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Person extends BaseEntity {

    @NonNull
    private String name;

    @JoinTable(
            name = "person_appointments",
            joinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "appointments_id", referencedColumnName = "id")}
    )
    private List<Appointment> appointments;
}
