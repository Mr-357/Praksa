package com.boemska.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Entity
@Table(name="tickets")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ticket {

    @Id
    @GenericGenerator(name ="custom_id",strategy = "com.boemska.helpers.UUIDGenerator")
    @GeneratedValue(generator = "custom_id")
    private String id;

    @Column(columnDefinition = "TEXT",length = 20,nullable = false)
    private String numbers;

    @Column
    private LocalDateTime created;

    public Ticket(String numbers){
        this.numbers=numbers;
        setCreated(LocalDateTime.now());

    }
    public Ticket() {
        setCreated(LocalDateTime.now());
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id=id;
    }


    public String getNumbers() {
        return numbers;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(numbers, ticket.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ArrayList<Integer> getNumberList(){
        ArrayList tmp = (ArrayList<Integer>) Arrays.asList(numbers.split(","))
                .stream()
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(toList());
        Collections.sort(tmp);
        return tmp;
    }
}
