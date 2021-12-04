package ru.otus.d13.atm.cells;

import ru.otus.d13.atm.pojo.Banknote;

public class UniBanknoteCellProvider implements BanknoteCellProvider {
    @Override
    public BanknoteCell getBanknoteCell(Banknote banknote, int count) {
        return new UniBanknoteCell(banknote,count);
    }
}
