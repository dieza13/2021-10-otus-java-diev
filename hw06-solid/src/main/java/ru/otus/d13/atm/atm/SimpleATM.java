package ru.otus.d13.atm.atm;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.otus.d13.atm.pojo.Banknote;
import ru.otus.d13.atm.slots.BanknoteSlot;

import java.util.List;
import java.util.Optional;

import static ru.otus.d13.atm.messages.Messages.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimpleATM implements ATM {

    private final BanknoteSlot banknoteSlot;

    public SimpleATM(BanknoteSlot banknoteSlot) {
        this.banknoteSlot = banknoteSlot;
    }


    /**
     * Пополнить
     *
     * @param banknotes         - пачка банкнот
     */
    @Override
    public void putBanknotes(List<Banknote> banknotes) {
        banknoteSlot.putBanknotes(banknotes);
    }

    /**
     * Выдать наличные, наличные выдаются пачками банкнот,
     * в пачке банкноты одного номинала
     *
     * @param sum               - запрошенная сумма
     * @return - список пачек банкнот с банкнотами одного ногминала в пачке
     */
    @Override
    public List<List<Banknote>> withdrawCash(int sum) {
        // Проверяем корректность запрошенной суммы, а также, что сумма не превышает сумму в банкомате
        Optional.of(sum)
                .map(val -> val <= 0 ? ERR_NEGATIVE_AMOUNT_REQUESTED : "empty")
                .map(val -> val.equals("empty") && sum > getBalance() ? ERR_ATM_CANT_ISSUE_SUM : val)
                .filter(val -> !val.equals("empty"))
                .ifPresent(err -> {
                    throw new RuntimeException(err);
                });

        return returnBanknotes(sum);
    }

    /**
     * Возвращаем список банкнот, соответствующий запрошенной сумме
     *
     * @param sum     - запрошенная сумма
     * @return -
     */
    private List returnBanknotes(int sum) {
        return banknoteSlot.issueSumInCash(sum);
    }

    /**
     * Баланс банкомата
     *
     * @return - сумма в банкомате
     */
    @Override
    public int getBalance() {
        return banknoteSlot.getBalance();
    }
}
