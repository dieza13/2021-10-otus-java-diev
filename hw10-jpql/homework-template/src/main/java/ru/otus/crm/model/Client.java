package ru.otus.crm.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Data
public class Client implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Phone> phones;

    public Client() {
        this(null,null,null, null);
    }

    public Client(String name) {
        this(null,name,null, null);
    }

    public Client(Long id, String name) {
        this(id,name,null, null);
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        if (phones == null)
            this.phones = new ArrayList<>();
        else
            setPhones(phones);
    }

    public Client(String name, Address address, List<Phone> phones) {
        this(null,name,address,phones);
    }

    @Override
    public Client clone() {
        return new Client(this.id, this.name);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setClient(this);
    }
    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setClient(null);
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
        phones.forEach(p->p.setClient(this));
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
