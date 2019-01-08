package com.davidstranders.chatbotapi.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Appointment extends BaseEntity {

    @NonNull
    private LocalDateTime start;

    @NonNull
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.EAGER)
    private Room room;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "appointment_person",
            joinColumns = {@JoinColumn(name = "appointment_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id", referencedColumnName = "id")}
    )
    private List<Person> persons;

    @Override
    public String toString() {
        Locale dutch=new Locale("nl", "NL");
        StringBuilder sb = new StringBuilder("Op ");
        sb.append(start.format(DateTimeFormatter.ofPattern("EEEE dd MMMM", dutch)));
        sb.append(" van ");
        this.addTimeString(sb, start);
        sb.append(" tot ");
        this.addTimeString(sb, end);
        if (room != null) {
            sb.append(" in " + room.getName());
        }
        if (persons != null && persons.size() > 0) {
            sb.append(" met ");
            sb.append(persons.stream()
                    .map(person -> person.getName())
                    .collect(Collectors.joining(" en ")));
        }
        return sb.toString();
    }

    private void addTimeString(StringBuilder sb, LocalDateTime dateTime){
        sb.append(dateTime.format(DateTimeFormatter.ofPattern("HH")));
        sb.append(" uur");
        if (!dateTime.format(DateTimeFormatter.ofPattern("mm")).equals("00")) {
            sb.append(dateTime.format(DateTimeFormatter.ofPattern(" mm")));
        }
    }
}
