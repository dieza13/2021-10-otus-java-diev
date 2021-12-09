package ru.otus.d13.atm.atm;

import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;

public interface ATM {
    void putBanknotes(List<Banknote> banknotes);
    <T> List<T> withdrawCash(int sum);
    int getBalance();

}
