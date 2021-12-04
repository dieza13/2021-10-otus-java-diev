package ru.otus.d13.atm.slots;

import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;

public interface BanknoteSlot {
    void putBanknotes(List<Banknote> banknotes);
    List issueSumInCash(int sum) throws RuntimeException;
    int getBalance();
}
