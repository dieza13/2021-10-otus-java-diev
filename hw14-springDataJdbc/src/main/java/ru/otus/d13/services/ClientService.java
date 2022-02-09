package ru.otus.d13.services;

import ru.otus.d13.models.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Client saveClient(Client client);

    Optional<Client> getClient(long id);

    List<Client> findAll();

}
