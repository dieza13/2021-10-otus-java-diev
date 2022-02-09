package ru.otus.d13.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.otus.d13.models.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {


}
