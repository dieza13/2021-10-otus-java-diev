package ru.otus.cache_vs_db;


import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientCached;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.jdbc.mapper.*;

import javax.sql.DataSource;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сравниваем скорость взаимодействия с базой с использованием самодельного кеша и без")
@Testcontainers
public class CacheVsDBTest {
    private static final Logger log = LoggerFactory.getLogger(CacheVsDBTest.class);

    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final String ERR_SAVE_GET_CLIENT = "Ошибка сохранения и дальнейшего запроса клиента по id";

    @Container
    private final static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:12-alpine")
            .withDatabaseName("testDataBase")
            .withUsername("owner")
            .withPassword("secret");

    private static TransactionRunnerJdbc transactionRunner;
    private static DataTemplateJdbc<Client> dataTemplateClient;


    @BeforeAll
    public static void initTests() {

        var dataSource = new DriverManagerDataSource(postgresqlContainer.getJdbcUrl(), postgresqlContainer.getUsername(), postgresqlContainer.getPassword());
        flywayMigrations(dataSource);
        var dbExecutor = new DbExecutorImpl();
        transactionRunner = new TransactionRunnerJdbc(dataSource);

        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient,entityClassMetaDataClient);

    }

    @Test
    public void compareDBAndDBWithCache() {

        int clientCount = 1500;

        var dbServiceClient = new DbServiceClientCached(transactionRunner, dataTemplateClient);
        var dbServiceClient_Old = new DbServiceClientImpl(transactionRunner, dataTemplateClient);


        long oldServiceExecTime = executeClientSaveGet(dbServiceClient_Old, clientCount);
        long newServiceExecTime = executeClientSaveGet(dbServiceClient, clientCount);

        log.info("Время вставки/получения без кеш: {}; время вставки/получения с кеш {}", oldServiceExecTime, newServiceExecTime);
        assertThat(oldServiceExecTime).isGreaterThan(newServiceExecTime);

    }

    private long executeClientSaveGet(DBServiceClient serviceClient, int clientCount) {
        long before = System.currentTimeMillis();
        IntStream.range(1,clientCount).forEach(num->{
            try {
                var client = serviceClient.saveClient(new Client(String.format("client{}",num)));
                var clientSelected = serviceClient.getClient(client.getId());
            } catch(Exception e) {
                throw new RuntimeException(ERR_SAVE_GET_CLIENT,e);
            }
        });
        long resultTime = (System.currentTimeMillis() - before);
        log.info("Время вставки и получения из базы {} клиентов состовляет {}", clientCount, resultTime);
        return resultTime;
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }

}
