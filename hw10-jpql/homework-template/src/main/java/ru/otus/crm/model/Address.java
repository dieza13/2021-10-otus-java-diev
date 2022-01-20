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

    @Column(name = "address_text")
    String addressText;

    public Address(Long id, String addressText) {
        this.id = id;
        this.addressText = addressText;
    }

    @Override
    public Address clone()  {
        return new Address(id,addressText);
    }

    public Address() {
    }
}
