package ru.otus.d13.models;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PlainClient {
    private final Client client;



    public PlainClient(Long id, String name, String address, String phone) {
        client = new Client(id, name, new Address(null,address/*, null*/), Set.of(new Phone(null, phone, null)));
    }

    public static PlainClient newInstance(Client client) {
        return new PlainClient(client);
    }
    private PlainClient(Client client) {
        this.client = client;
    }

    public Long getId() {
        return client.getId();
    }

    public String getName() {
        return client.getName();
    }

    public String getAddress() {
        return client.getAddress() == null ? "" : client.getAddress().getStreet();
    }

    public String getPhone() {
        return client.getPhones() != null && getClientPhoneToShow().isPresent() ? getClientPhoneToShow().get().getNumber() : "";
    }

    private Optional<Phone> getClientPhoneToShow() {
        return client.getPhones().stream().findFirst();
    }

    @Transient
    public Client getPureClient() {
        return client;
    }


}
