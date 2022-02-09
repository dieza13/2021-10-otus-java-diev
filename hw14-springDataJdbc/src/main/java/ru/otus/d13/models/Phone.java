package ru.otus.d13.models;

import lombok.AccessLevel;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;


@Table("phone")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Phone {
    @Id
    private
    Long id;
    private String number;
    private Long client_id;

    public Phone(String number, Long client_id) {
        this(null, number, client_id);
    }

    @PersistenceConstructor
    public Phone(Long id, String number, Long client_id) {
        this.id = id;
        this.number = number;
        this.client_id = client_id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setClient_id(Long client_id) {
        this.client_id = client_id;
    }

    public Long getId() {
        return this.id;
    }

    public String getNumber() {
        return this.number;
    }

    public Long getClient_id() {
        return this.client_id;
    }
}
