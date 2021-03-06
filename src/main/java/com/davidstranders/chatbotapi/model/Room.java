package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Room extends BaseEntity {

    @NonNull
    private String name;

    @NonNull
    private Integer number;

    @OneToMany(mappedBy = "room")
    private List<Appointment> appointments;
}
