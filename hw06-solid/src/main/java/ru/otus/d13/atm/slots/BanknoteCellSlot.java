package ru.otus.d13.atm.slots;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.otus.d13.atm.cells.BanknoteCellProvider;
import ru.otus.d13.atm.pojo.Banknote;
import ru.otus.d13.atm.cells.BanknoteCell;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.summingInt;
import static ru.otus.d13.atm.messages.Messages.ERR_ATM_CANT_ISSUE_SUM;

/**
 * Слот ячеек банкомата
 */
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class BanknoteCellSlot implements BanknoteSlot {

    Map<Banknote, BanknoteCell> slot;
    BanknoteCellProvider banknoteCellProvider;

    public BanknoteCellSlot(Map<Banknote, Integer> banknotes, BanknoteCellProvider banknoteCellProvider) {
        this.banknoteCellProvider = banknoteCellProvider;
        slot = initSlot(banknotes);

    }

    /**
     * Установка параметров слота
     * @param banknotes - Список банкнот и их количество
     * @return - словарь ячеек, каждая из которых соответствует определенному типу банкноты
     */
    private Map<Banknote, BanknoteCell> initSlot(Map<Banknote, Integer> banknotes) {
        return banknotes.entrySet().stream()
                .sorted((e1,e2)-> Integer.compare(e2.getKey().getNominal(),e1.getKey().getNominal()))
                .map(e->banknoteCellProvider.getBanknoteCell(e.getKey(),e.getValue()))
                .collect(Collectors.toMap(BanknoteCell::getBanknote, Function.identity(),(oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * Поместить банкноты в соответствующие ячейки
     * @param banknotes - Пачка банкнот
     */
    public void putBanknotes(List<Banknote> banknotes) {
        banknotes.stream()
                .collect(Collectors.groupingBy(x -> x, summingInt(x -> 1)))
                .forEach((banknote,count)->slot.get(banknote).putBanknotes(count));
    }

    /**
     * Выдать наличные
     * @param sum - запрашиваемая сумма
     * @return - Список банкнот объединенных по номиналу
     */
    public List<List<Banknote>> issueSumInCash(int sum) throws RuntimeException{
        final List<List<Banknote>> banknotes = new ArrayList<>();
        int currentAmount = sum;

        for (BanknoteCell bc : slot.values()) {
            if (bc.getTotalAmount() > 0 && currentAmount / bc.nominal() >= 1) {
                int count = Math.min(currentAmount,bc.getTotalAmount()) / bc.nominal();
                banknotes.add(bc.getBanknotes(count));
                currentAmount -= bc.nominal() * count;
            }
        }

        if (currentAmount != 0){
            throw new RuntimeException(ERR_ATM_CANT_ISSUE_SUM);
        }
        return banknotes;
    }

    /**
     * общий баланс
     * @return - общая сумма
     */
    @Override
    public int getBalance() {
        return slot.values()
                .stream()
                .map(BanknoteCell::getTotalAmount)
                .reduce(Integer::sum)
                .orElse(0);
    }
}
