package ru.otus.d13.atm.cells;

import lombok.Getter;
import lombok.Setter;
import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;

@Getter
@Setter
public abstract class BanknoteCell<T extends Banknote> {


    private int count;
    private T banknote;

    BanknoteCell(int count, T banknote) {
        this.count = count;
        this.banknote = banknote;

    }

    public abstract void putBanknotes(int count);

    public abstract List<T> getBanknotes(int count);

    public abstract int nominal();

    public abstract int getTotalAmount();
}
