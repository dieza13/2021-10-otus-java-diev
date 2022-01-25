package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionRunner;
import ru.otus.crm.model.Manager;
import java.util.List;
import java.util.Optional;

public class DbServiceManagerImpl implements DBServiceManager {
    private static final Logger log = LoggerFactory.getLogger(DbServiceManagerImpl.class);

    private final DataTemplate<Manager> managerDataTemplate;
    private final TransactionRunner transactionRunner;
    private final HwCache<Long, Manager> managerCache;

    public DbServiceManagerImpl(TransactionRunner transactionRunner, DataTemplate<Manager> managerDataTemplate) {
        this.transactionRunner = transactionRunner;
        this.managerDataTemplate = managerDataTemplate;
        this.managerCache = new MyCache<>();
    }

    @Override
    public Manager saveManager(Manager manager) {
        return transactionRunner.doInTransaction(connection -> {
            Manager persistedManager;
            if (manager.getNo() == null) {
                var managerNo = managerDataTemplate.insert(connection, manager);
                persistedManager = new Manager(managerNo, manager.getLabel(), manager.getParam1());
                log.info("created manager: {}", persistedManager);
            } else {
                managerDataTemplate.update(connection, manager);
                persistedManager = manager;
            }
            log.info("updated manager: {}", persistedManager);
            managerCache.put(persistedManager.getNo(), persistedManager);
            return persistedManager;
        });
    }

    @Override
    public Optional<Manager> getManager(long no) {
        return transactionRunner.doInTransaction(connection -> {
            if (managerCache.get(no) == null) {
                managerDataTemplate.findById(connection, no).ifPresent(m->managerCache.put(no,m));
            }
            Optional<Manager> managerOptional = Optional.ofNullable(managerCache.get(no));
            log.info("manager: {}", managerOptional);
            return managerOptional;
        });
    }

    @Override
    public List<Manager> findAll() {
        return transactionRunner.doInTransaction(connection -> {
            var managerList = managerDataTemplate.findAll(connection);
            log.info("managerList:{}", managerList);
            return managerList;
       });
    }
}
