package com.boemska.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
