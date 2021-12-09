package ru.otus.d13.atm.cells;

import lombok.Getter;
import lombok.Setter;
import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;

@Getter
@Setter
public abstract class BanknoteCell {


    private int count;
    private Banknote banknote;

    BanknoteCell(int count, Banknote banknote) {
        this.count = count;
        this.banknote = banknote;

    }

    public abstract void putBanknotes(int count);

    public abstract List<Banknote> getBanknotes(int count);

    public abstract int nominal();

    public abstract int getTotalAmount();
}
