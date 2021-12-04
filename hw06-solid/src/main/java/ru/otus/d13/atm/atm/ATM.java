package ru.otus.d13.atm.atm;

import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;

public interface ATM {
    void putBanknotes(String accountHolderName, List<Banknote> banknotes);
    <T> List<T> withdrawCash(String accountHolderName, int sum);
    int getAccountBalance(String accountHolderName);
    int getBalance();

}
