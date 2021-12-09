package ru.otus.d13.atm.pojo;

import lombok.Getter;

@Getter
public enum RubBanknote implements Banknote{
    RUB100(100),
    RUB200(200),
    RUB500(500),
    RUB1000(1000),
    RUB2000(2000),
    RUB5000(5000);

    int nominal;

    RubBanknote(int nominal) {
        this.nominal = nominal;
    }
}