package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.crm.model.Client;
import ru.otus.core.sessionmanager.TransactionRunner;

import java.util.List;
import java.util.Optional;

public class DbServiceClientCached implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientCached.class);

    private final DataTemplate<Client> dataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<Long, Client> clientCache;

    public DbServiceClientCached(TransactionRunner transactionRunner, DataTemplate<Client> dataTemplate) {
        this.transactionRunner = transactionRunner;
        this.dataTemplate = dataTemplate;
        this.clientCache = new MyCache<>();
    }

    @Override
    public Client saveClient(Client client) {
        return transactionRunner.doInTransaction(connection -> {
            Client persistedClient;
            if (client.getId() == null) {
                var clientId = dataTemplate.insert(connection, client);
                persistedClient = new Client(clientId, client.getName());
                log.info("created client: {}", persistedClient);
            } else {
                dataTemplate.update(connection, client);
                persistedClient = client;
            }
            log.info("updated client: {}", persistedClient);
            clientCache.put(persistedClient.getId(), persistedClient);
            return persistedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        return transactionRunner.doInTransaction(connection -> {
            if (clientCache.get(id) == null) {
                dataTemplate.findById(connection, id).ifPresent(m->clientCache.put(id,m));
            }
            Optional<Client> clientOptional = Optional.ofNullable(clientCache.get(id));
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var clientList = dataTemplate.findAll(connection);
            log.info("clientList:{}", clientList);
            return clientList;
       });
    }
}
