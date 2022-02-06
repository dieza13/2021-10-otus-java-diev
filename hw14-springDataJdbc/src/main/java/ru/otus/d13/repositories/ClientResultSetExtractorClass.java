package ru.otus.d13.repositories;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.d13.models.Address;
import ru.otus.d13.models.Client;
import ru.otus.d13.models.Phone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClientResultSetExtractorClass implements ResultSetExtractor<List<Client>> {

    @Override
    public List<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {
        var clientList = new ArrayList<Client>();
        Long prevClientId = null;
        while (rs.next()) {
            var clientId = rs.getLong("id");
            Client client = null;
            if (prevClientId == null || !prevClientId.equals(clientId)) {
                Long addressId = rs.getLong("address_id");

                client = new Client(
                        clientId, rs.getString("name"),
                        new Address(addressId, rs.getString("street")/*,null*/),
                        new HashSet<>());
                clientList.add(client);
                prevClientId = clientId;
            }
            Long phoneId = (Long) rs.getObject("phone_id");
            if (client != null && phoneId != null) {
                client.getPhones().add(new Phone(phoneId, rs.getString("number"), clientId));
            }
        }
        return clientList;
    }
}
