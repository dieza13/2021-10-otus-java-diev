package ru.otus.crm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "phone")
public class Phone {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "number")
    String number;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_id", nullable = false)
    Client client;


    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    public Phone() {
    }
}
