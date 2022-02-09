package ru.otus.d13.repositories;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.d13.models.Client;

import java.util.List;

public interface ClientRepository extends CrudRepository<Client, Long> {

    @Override
    @Query(name = "Client.findAll", resultSetExtractorClass = ClientResultSetExtractorClass.class)
    List<Client> findAll();

    @Modifying
    @Query(name = "Client.updateClientAddressId")
    void updateClientAddressId(@Param("clientId") Long clientId, @Param("addressId") Long addressId);

}
