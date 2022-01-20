package ru.otus.crm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter @ToString
@Entity
@Table(name = "phone")
public class Phone {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "number")
    String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="client_id", nullable = false)
    Client client;


    public Phone(Long id, String number) {
        this.id = id;
        this.number = number;
    }

    @Override
    public Phone clone()  {
        return new Phone(id,number);
    }

    public Phone() {
    }
}
