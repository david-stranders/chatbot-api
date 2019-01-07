package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Person extends BaseEntity {

    @NonNull
    private String name;

    @ManyToMany(mappedBy = "persons")
    private List<Appointment> appointments;
}
