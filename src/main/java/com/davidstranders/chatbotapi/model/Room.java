package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Room extends BaseEntity {

    @NonNull
    private String name;

    @OneToMany(mappedBy = "attendees")
    private List<Appointment> appointments;
}
