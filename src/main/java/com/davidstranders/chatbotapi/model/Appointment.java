package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"id"})
public class Appointment extends BaseEntity {

    @NonNull
    private LocalDateTime start;

    @NonNull
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER)
    private Room room;

    @ManyToMany(mappedBy = "appointments", fetch = FetchType.EAGER)
    private List<Person> persons;
}
