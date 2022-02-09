package ru.otus.d13.models;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;

@Table("client")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class Client {

    @Id
    Long id;
    String name;
    @MappedCollection(idColumn = "id")
    Address address;
    @MappedCollection(idColumn = "client_id")
    Set<Phone> phones;

    public Client(String name, Address address, Set<Phone> phones) {
        this(null, name, address, phones);
    }

    @PersistenceConstructor
    public Client(Long id, String name, Address address, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
//        if (address != null)
//            this.address.getClient().add(this);
        this.phones = phones;
    }

}
