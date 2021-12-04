package ru.otus.d13.atm.pojo;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    final String owner;

    Integer amount;

    public void increaseAmount(int amount) {
        this.amount += amount;
    }
    public void decreaseAmount(int amount) {
       this.amount -= amount;
    }
    public Account copy() {
        return new Account(owner,amount);
    }

}
