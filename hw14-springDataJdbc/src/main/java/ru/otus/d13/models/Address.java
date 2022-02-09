package ru.otus.d13.models;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;


@Table("address")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    private
    Long id;
    private String street;
//    @MappedCollection(idColumn = "address_id")
//    private Set<Client> client = new HashSet<>();

    public Address(String street, Set<Client> client) {
        this.id = null;
        this.street = street;
//        this.client = client;
    }

    @PersistenceConstructor
    public Address(Long id, String street/*, Set<Client> client*/) {
        this.id = id;
        this.street = street;
//        this.client = (client == null) ? new HashSet<>() : client;
    }

    public Long getId() {
        return this.id;
    }

    public String getStreet() {
        return this.street;
    }

//    public Set<Client> getClient() {
//        return this.client;
//    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStreet(String street) {
        this.street = street;
    }

//    public void setClient(Set<Client> client) {
//        this.client = client;
//    }
}
