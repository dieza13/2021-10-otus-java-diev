package ru.otus.d13.atm.cells;

import ru.otus.d13.atm.pojo.Banknote;

public interface BanknoteCellProvider {
    BanknoteCell getBanknoteCell(Banknote banknote, int count);
}
