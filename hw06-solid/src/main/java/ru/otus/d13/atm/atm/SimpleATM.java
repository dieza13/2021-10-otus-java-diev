package ru.otus.d13.atm.atm;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.otus.d13.atm.pojo.Account;
import ru.otus.d13.atm.pojo.Banknote;
import ru.otus.d13.atm.slots.BanknoteSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.otus.d13.atm.messages.Messages.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SimpleATM implements ATM {

    private final Map<String, Account> accounts;
    private final BanknoteSlot banknoteSlot;

    public SimpleATM(List<Account> accounts, BanknoteSlot banknoteSlot) {
        this.accounts = accounts
                .stream()
                .map(Account::copy)
                .collect(Collectors.toMap(Account::getOwner, Function.identity()));
        this.banknoteSlot = banknoteSlot;
    }


    /**
     * Пополнить счет
     *
     * @param accountHolderName - держатель счета
     * @param banknotes         - пачка банкнот
     */
    @Override
    public void putBanknotes(String accountHolderName, List<Banknote> banknotes) {
        Account account = accounts.get(accountHolderName);
        account.increaseAmount(banknotes
                .stream()
                .map(Banknote::getNominal)
                .reduce(Integer::sum)
                .orElse(0));
        banknoteSlot.putBanknotes(banknotes);
    }

    /**
     * Выдать наличные по имени деражателя счета, наличные выдаются пачками банкнот,
     * в пачке банкноты одного номинала
     *
     * @param accountHolderName - имя держателя счета
     * @param sum               - запрошенная сумма
     * @return - список пачек банкнот с банкнотами одного ногминала в пачке
     */
    @Override
    public List<List<Banknote>> withdrawCash(String accountHolderName, int sum) {
        // Проверяем корректность запрошенной суммы, а также, что сумма не превышает сумму в банкомате и на счете клиента
        Optional.of(sum)
                .map(val -> val <= 0 ? ERR_NEGATIVE_AMOUNT_REQUESTED : "empty")
                .map(val -> val.equals("empty") && sum > getBalance() ? ERR_ATM_CANT_ISSUE_SUM : val)
                .map(val -> val.equals("empty") && sum > getAccountBalance(accountHolderName) ? ERR_NOT_ENOUGH_FUNDS : val)
                .filter(val -> !val.equals("empty"))
                .ifPresent(err -> {
                    throw new RuntimeException(err);
                });

        return returnBanknotes(sum, accounts.get(accountHolderName));
    }

    /**
     * Возвращаем список банкнот, соответствующий запрошенной сумме
     *
     * @param sum     - запрошенная сумма
     * @param account - счет клиента
     * @return -
     */
    private List returnBanknotes(int sum, Account account) {
        return Optional.of(sum).filter(summ -> summ <= account.getAmount()).map(summ -> {
            List banknotes = banknoteSlot.issueSumInCash(summ);
            if (Optional.ofNullable(banknotes).isPresent()) {
                account.decreaseAmount(summ);
            }
            return banknotes;
        }).get();
    }

    /**
     * Запрос баланса счета
     *
     * @param accountHolderName - Имя вдадельца
     * @return - сумма на счете
     */
    @Override
    public int getAccountBalance(String accountHolderName) {
        return accounts.get(accountHolderName).getAmount();
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
