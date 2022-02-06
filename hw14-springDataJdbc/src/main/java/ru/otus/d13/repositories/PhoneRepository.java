package ru.otus.d13.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.otus.d13.models.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Long> {


}
