package ru.otus.d13.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.d13.models.Client;
import ru.otus.d13.repositories.ClientRepository;
import ru.otus.d13.sessionmanager.TransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {

    TransactionManager transactionManager;
    ClientRepository clientRepository;

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(()->{
            Client sClient = clientRepository.save(client);
            clientRepository.updateClientAddressId(client.getId(),client.getAddress().getId());
            log.info("client saved: {} ",sClient);
            return sClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        Optional<Client> client = clientRepository.findById(id);
        log.info("client getted {}", client);
        return client;
    }

    @Override
    public List<Client> findAll() {
        var clientList = new ArrayList<Client>();
        clientList.addAll(clientRepository.findAll());
        log.info("clientList:{}", clientList);
        return clientList;
    }
}
