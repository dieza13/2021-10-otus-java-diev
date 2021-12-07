package ru.otus.d13.atm.cells;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.otus.d13.atm.pojo.Banknote;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.otus.d13.atm.messages.Messages.ERR_TO_LITTLE_BANKNOTES;

/**
 * Ячейка банкомата
 * @param <T> - тип банкнот в ячейке
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UniBanknoteCell<T extends Banknote> extends BanknoteCell<T> {


    public UniBanknoteCell(T banknote, int count) {
        super(count, banknote);
    }

    /**
     * Загрузить банкноты в ячейку
     * @param count - количество банкот
     */
    @Override
    public void putBanknotes(int count) {
        increaseCount(count);
    }

    /**
     * Получить пачку банкнот
     * @param count - запрашиваемое количество
     * @return - пачка банкот
     */
    @Override
    public List<T> getBanknotes(int count) {
        if (this.getCount() < count)
            throw new RuntimeException(ERR_TO_LITTLE_BANKNOTES);
        List<T> banknotes = Stream.generate(this::getBanknote).limit(count).collect(Collectors.toList());
        decreaseCount(count);
        return banknotes;
    }

    /**
     * Увеличить количество банкот
     * @param count - на сколько увеличить
     */
    private void increaseCount(int count) {
        setCount(getCount() + count);
    }

    /**
     * Уменьшить количество банкот
     * @param count - на сколько уменьшить
     */
    private void decreaseCount(int count) {
        setCount(getCount() - count);
    }

    /**
     * Номинал банкнот в ячейке
     * @return - номинал
     */
    @Override
    public int nominal() {
        return getBanknote().getNominal();
    }

    /**
     * Получить сумму банкот в ячейке
     * @return сумма банкнот
     */
    @Override
    public int getTotalAmount() {
        return getCount() * getBanknote().getNominal();
    }


}
