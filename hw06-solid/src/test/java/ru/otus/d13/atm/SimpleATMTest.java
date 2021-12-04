package ru.otus.d13.atm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.d13.atm.atm.ATM;
import ru.otus.d13.atm.atm.SimpleATM;
import ru.otus.d13.atm.cells.UniBanknoteCellProvider;
import ru.otus.d13.atm.pojo.Account;
import ru.otus.d13.atm.pojo.Banknote;
import ru.otus.d13.atm.pojo.RubBanknote;
import ru.otus.d13.atm.slots.BanknoteCellSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.d13.atm.messages.Messages.*;

public class SimpleATMTest {

    ATM atm;
    List<String> output;

    List<Account> accounts = Arrays.asList(
            new Account("Карабас", 100000)
            , new Account("Буратино", 15000)
            , new Account("Пьерро", 5000));

    @BeforeEach
    void initTest() {
        output = new ArrayList<>();
        output.add("<---------START TEST--------->");
        Map<Banknote, Integer> banknoteCountMap = Arrays.stream(RubBanknote.values()).collect(Collectors.toMap(Function.identity(), b -> 5));
        BanknoteCellSlot slot = new BanknoteCellSlot(banknoteCountMap, new UniBanknoteCellProvider());
        atm = new SimpleATM(accounts, slot);
        output.add(String.format("Клиенты: %s", accounts
                .stream()
                .map(Account::toString)
                .collect(Collectors.joining(";", "[", "]"))));
        output.add(String.format("В банкомате купюры номиналом: %s", banknoteCountMap));
        output.add(String.format("На общую сумму: %s\n", banknoteCountMap.entrySet()
                .stream()
                .map(e -> e.getKey().getNominal() * e.getValue())
                .reduce(Integer::sum).orElse(0)));
    }

    @AfterEach
    void tearDownTest() {
        output.add("<---------STOP TEST--------->\n");
        output.forEach(System.out::println);
    }

    @Test
    @DisplayName("Пополнение счета банкнотами разного номинала")
    public void putBanknotesDifferentNominal() {
        output.add("!!!Пополнить счет банкнотами разного номинала!!!");

        // каждый внесет на счет по 8800
        List<Banknote> banknotes = Arrays.asList(RubBanknote.values());
        accounts.forEach(account -> putBanknotesCheck(banknotes, account.getOwner(), atm));

    }

    @Test
    @DisplayName("Выдача запрошенной суммы")
    public void getSumma() {
        output.add("!!!Выдавать запрошенную сумму!!!");

        // каждый пробует получить всю сумму со счета
        accounts.forEach(account -> getSummaCheck(account.getOwner(), account.getAmount(), atm));

        // Буратино пробует получить отрицательную сумму
        getSummaCheck(accounts.get(1).getOwner(), -5000, atm);

        // Буратино пробует получить сумму больше, чем у него есть
        getSummaCheck(accounts.get(1).getOwner(), 17000, atm);

        // Карабас пробует получить сумму банкнотами разных номиналов
        getSummaCheck(accounts.get(0).getOwner(), 13800, atm);
    }

    @Test
    @DisplayName("Сумма остатка денежных средств")
    public void getBalanceOfFunds() {
        output.add("!!!Сумма остатка денежных средств!!!");
        // каждый пробует получить 7300
        int sum = 4300;
        List<Integer> balancesBefore = accounts.stream().map(a->atm.getAccountBalance(a.getOwner())).collect(Collectors.toList());
        int atmBalanceBefore = atm.getBalance();
        accounts.forEach(account -> atm.withdrawCash(account.getOwner(), sum));
        List<Integer> balancesAfter = accounts.stream().map(a->atm.getAccountBalance(a.getOwner())).collect(Collectors.toList());
        int atmBalanceAfter = atm.getBalance();
        getBalanceOfFundsCheck(balancesBefore,balancesAfter,accounts,atmBalanceBefore,atmBalanceAfter,sum);
    }

    /**
     * Проверка баланса счетов до и после операции
     * @param balancesBefore - балансы счетов до операции снятия
     * @param balancesAfter - балансы счетов после операции снятия
     * @param accounts - счета
     * @param atmBalanceBefore - общий баланс банкомата до операции снятия
     * @param atmBalanceAfter - общий баланс банкомата после операции снятия
     * @param chgAmount - снимаемая сумма
     */
    void getBalanceOfFundsCheck(List<Integer> balancesBefore
                               ,List<Integer> balancesAfter
                               ,List<Account> accounts
                               ,int atmBalanceBefore
                               ,int atmBalanceAfter
                               ,int chgAmount) {
        output.add(String.format("---Каждый клиент снял со счета по %d",chgAmount));
        for (int i = 0; i < accounts.size(); i++) {
            output.add(String.format("%s было/стало: %d/%d", accounts.get(i).getOwner(),balancesBefore.get(i),balancesAfter.get(i)));
            int resWithChg = balancesAfter.get(i) + chgAmount;
            assertThat(resWithChg).isEqualTo(balancesBefore.get(i));
        }
        int resBWithChg = chgAmount * 3 + atmBalanceAfter;
        assertThat(resBWithChg).isEqualTo(atmBalanceBefore);
        output.add(String.format("Баланс до/после снятий: %d/%d", atmBalanceBefore, atmBalanceAfter));
    }

    /**
     * Проверка пополнения счета
     * @param banknotes - пачка банкнот
     * @param accountOwnerName - держатель счета
     * @param atm - банкомат
     */
    void putBanknotesCheck(List<Banknote> banknotes, String accountOwnerName, ATM atm) {
        int accountBalanceBefore = atm.getAccountBalance(accountOwnerName);
        int atmBalanceBefore = atm.getBalance();
        int depositedAmount = Arrays
                .stream(RubBanknote.values())
                .map(Banknote::getNominal)
                .reduce(Integer::sum).orElse(0);
        atm.putBanknotes(accountOwnerName, banknotes);
        // проверим изменения на счете и в банкомате и выдадим результат проверки
        checkBalanceChg(BalanceChangeAction.PUT
                , accountOwnerName
                , accountBalanceBefore
                , atm.getAccountBalance(accountOwnerName)
                , atmBalanceBefore
                , atm.getBalance()
                , depositedAmount);
    }

    /**
     * Проверка выдачи наличных
     * @param account - счет клиента
     * @param summ - запрашиваемая сумма
     * @param atm - банкомат
     */
    void getSummaCheck(String account, Integer summ, ATM atm) {
        int chgSum = 0;
        SimpleATM sAtm = (SimpleATM) atm;
        int accountBalanceBefore = atm.getAccountBalance(account);
        int atmBalanceBefore = atm.getBalance();
        Exception exp = null;
        List<List<Banknote>> banknotes = null;
        try {
            banknotes = atm.withdrawCash(account, summ);
        } catch (Exception e) {
            exp = e;
            e.printStackTrace();
        }
        // Провеим, что банкомат не выдает недопустимые суммы
        if (summ <= 0) {
            assertThat(exp).hasMessage(ERR_NEGATIVE_AMOUNT_REQUESTED);
        } else if (summ > atmBalanceBefore) {
            assertThat(exp).hasMessage(ERR_ATM_CANT_ISSUE_SUM);
        } else if (summ > accountBalanceBefore) {
            assertThat(exp).hasMessage(ERR_NOT_ENOUGH_FUNDS);
        } else {

            // проверим, что сумма полученных банкнот = запрошенной сумме
            chgSum = banknotesSum(banknotes);
            assertThat(summ).isEqualTo(chgSum);
            // проверим, что банкноты в каждой пачке одного номинала
            assertThat(isBanknotesHasSameNominalInBatch(banknotes)).isTrue();
        }
        // проверим изменения на счете и в банкомате и выдадим результат проверки
        checkBalanceChg(BalanceChangeAction.GET
                , account
                , accountBalanceBefore
                , atm.getAccountBalance(account)
                , atmBalanceBefore
                , atm.getBalance()
                , chgSum);
        output.add(String.format("%s хотел снять %d", account, summ));
    }

    /**
     * Проверка баланса счета и банкомата до и после изменения
     * @param operation - тип операции (PUT/GET)
     * @param account - счет
     * @param accountBalanceBefore - баланс счета до
     * @param accountBalanceAfter - баланс счета после
     * @param atmBalanceBefore - баланс банкомата до
     * @param atmBalanceAfter - баланс банкомата после
     * @param chgAmount - сумма операции
     */
    public void checkBalanceChg(BalanceChangeAction operation
            , String account
            , int accountBalanceBefore
            , int accountBalanceAfter
            , int atmBalanceBefore
            , int atmBalanceAfter
            , int chgAmount) {
        String oper = (operation == BalanceChangeAction.PUT) ? "внесения" : "получения";
        String oper2 = (operation == BalanceChangeAction.PUT) ? "внес" : "получил";
        output.add(String.format("---Клиентский счет %s до/после %s средств: %d/%d", account, oper, accountBalanceBefore, accountBalanceAfter));
        output.add(String.format("Общая сумма в банкомате до/после %s средств: %d/%d ", oper, atmBalanceBefore, atmBalanceAfter));
        output.add(String.format("%s %s сумму: %d", account, oper2, chgAmount));
        int accountAmountAfter = accountBalanceBefore + ((operation == BalanceChangeAction.PUT) ? chgAmount : -chgAmount);
        int atmAmountAfter = atmBalanceBefore + ((operation == BalanceChangeAction.PUT) ? chgAmount : -chgAmount);
        assertThat(accountAmountAfter).isEqualTo(accountBalanceAfter);
        assertThat(atmAmountAfter).isEqualTo(atmBalanceAfter);
    }

    /**
     * общая умма всех банкнот во всех пачках
     *
     * @param banknotes - банкноты по пачкам
     * @return сумма
     */
    int banknotesSum(List<List<Banknote>> banknotes) {
        return banknotes
                .stream()
                .map(l -> l
                        .stream()
                        .map(Banknote::getNominal)
                        .reduce(Integer::sum).orElse(0))
                .reduce(Integer::sum).orElse(0);
    }

    /**
     * Проверим, а в каждой ли пачке банкноты одного номинала
     * @param banknotes - список пачек бонкнот
     * @return - результат проверки одного номинал банкнот в пачке
     */
    boolean isBanknotesHasSameNominalInBatch(List<List<Banknote>> banknotes) {
        return banknotes
                .stream()
                .filter(list -> list
                        .stream()
                        .anyMatch(b -> b.getNominal() != list.get(0).getNominal())
                )
                .findFirst()
                .isEmpty();
    }

    /**
     * Тип операции
     */
    enum BalanceChangeAction {
        PUT,
        GET
    }


}
