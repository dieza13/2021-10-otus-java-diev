package ru.otus.crm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(name = "address")
public class Address  {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @Column(name = "address_text")
    String address_text;

    public Address(Long id, String address_text) {
        this.id = id;
        this.address_text = address_text;
    }

    public Address() {
    }
}
