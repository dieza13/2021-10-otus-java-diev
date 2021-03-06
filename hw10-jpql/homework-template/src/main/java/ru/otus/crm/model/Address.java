package ru.otus.crm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter @ToString
@Entity
@Table(name = "address")
public class Address implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "street")
    String street;

    public Address(Long id, String street) {
        this.id = id;
    }

    @Override
    public Address clone()  {
        return new Address(id, street);
    }

    public Address() {
    }
}
