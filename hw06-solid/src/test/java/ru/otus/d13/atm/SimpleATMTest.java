package ru.otus.d13.atm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.d13.atm.atm.ATM;
import ru.otus.d13.atm.atm.SimpleATM;
import ru.otus.d13.atm.cells.UniBanknoteCellProvider;
import ru.otus.d13.atm.pojo.Banknote;
import ru.otus.d13.atm.pojo.RubBanknote;
import ru.otus.d13.atm.slots.BanknoteCellSlot;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.d13.atm.messages.Messages.*;

public class SimpleATMTest {

    ATM atm;
    List<String> output;

    List<Integer> sums = Arrays.asList(100000, 15000, 5000);

    @BeforeEach
    void initTest() {
        output = new ArrayList<>();
        output.add("<---------START TEST--------->");
        Map<Banknote, Integer> banknoteCountMap = Arrays.stream(RubBanknote.values()).collect(Collectors.toMap(Function.identity(), b -> 5));
        BanknoteCellSlot slot = new BanknoteCellSlot(banknoteCountMap, new UniBanknoteCellProvider());
        atm = new SimpleATM(slot);
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
    @DisplayName("Внесение банкнот разного номинала")
    public void putBanknotesDifferentNominal() {
        output.add("!!!Внести банкноты разного номинала!!!");

        // пытаемся внести пачку банкнот на сумму 8800
        List<Banknote> banknotes = Arrays.asList(RubBanknote.values());
        putBanknotesCheck(banknotes, atm);

    }

    @Test
    @DisplayName("Выдача запрошенной суммы")
    public void getSumma() {
        output.add("!!!Выдавать запрошенную сумму!!!");

        // пробуем получить разные суммы
        sums.forEach(sum -> getSummaCheck(sum, atm));

        // пробуем получить отрицательную сумму
        getSummaCheck(-5000, atm);

        // пробуем получить сумму банкнотами разных номиналов
        List<List<Banknote>> banknotes = getSummaCheck(13800, atm);
        String res = "Пачки содержат банкноты следующих номиналов: " +
                banknotes.stream().map(l->String.valueOf(l.size())
                        .concat(" по ")
                        .concat(String.valueOf(l.get(0).getNominal()))
                        .concat(";")).reduce((a,b)->a + b);
        output.add(res);
    }

    /**
     * Проверка пополнения
     * @param banknotes - пачка банкнот
     * @param atm - банкомат
     */
    void putBanknotesCheck(List<Banknote> banknotes, ATM atm) {

        output.add(String.format("Пытаемся внести пачку банкнот номиналами: %s", banknotes));
        int atmBalanceBefore = atm.getBalance();
        // общая сумма банкнот, которые вносим
        int depositedAmount = Arrays
                .stream(RubBanknote.values())
                .map(Banknote::getNominal)
                .reduce(Integer::sum).orElse(0);
        atm.putBanknotes(banknotes);
        // проверим изменения в банкомате и выдадим результат проверки
        checkBalanceChg(BalanceChangeAction.PUT
                , atmBalanceBefore
                , atm.getBalance()
                , depositedAmount);
    }

    /**
     * Проверка выдачи наличных
     * @param summ - запрашиваемая сумма
     * @param atm - банкомат
     */
    List<List<Banknote>> getSummaCheck(Integer summ, ATM atm) {
        output.add(String.format("Пытаемся получить сумму: %d", summ));
        int chgSum = 0;
        SimpleATM sAtm = (SimpleATM) atm;
        int atmBalanceBefore = atm.getBalance();
        Exception exp = null;
        List<List<Banknote>> banknotes = null;
        try {
            banknotes = atm.withdrawCash(summ);
        } catch (Exception e) {
            exp = e;
            e.printStackTrace();
        }
        // Проверим, что банкомат не выдает недопустимые суммы
        if (summ <= 0) {
            assertThat(exp).hasMessage(ERR_NEGATIVE_AMOUNT_REQUESTED);
        } else if (summ > atmBalanceBefore) {
            assertThat(exp).hasMessage(ERR_ATM_CANT_ISSUE_SUM);
        } else {
            // проверим, что сумма полученных банкнот = запрошенной сумме
            chgSum = banknotesSum(banknotes);
            assertThat(summ).isEqualTo(chgSum);
            // проверим, что банкноты в каждой пачке одного номинала
            assertThat(isBanknotesHasSameNominalInBatch(banknotes)).isTrue();
        }
        // проверим изменения в банкомате и выдадим результат проверки
        checkBalanceChg(BalanceChangeAction.GET
                , atmBalanceBefore
                , atm.getBalance()
                , chgSum);
        return banknotes;
    }

    /**
     * Проверка баланса банкомата до и после изменения
     * @param operation - тип операции (PUT/GET)
     * @param atmBalanceBefore - баланс банкомата до
     * @param atmBalanceAfter - баланс банкомата после
     * @param chgAmount - сумма операции
     */
    public void checkBalanceChg(BalanceChangeAction operation
            , int atmBalanceBefore
            , int atmBalanceAfter
            , int chgAmount) {
        String oper = (operation == BalanceChangeAction.PUT) ? "внесения" : "получения";
        String oper2 = (operation == BalanceChangeAction.PUT) ? "внесена" : "получена";
        output.add(String.format("Общая сумма в банкомате до/после %s средств: %d/%d ", oper, atmBalanceBefore, atmBalanceAfter));
        output.add(String.format("%s сумма: %d", oper2, chgAmount));
        int atmAmountAfter = atmBalanceBefore + ((operation == BalanceChangeAction.PUT) ? chgAmount : -chgAmount);
        assertThat(atmAmountAfter).isEqualTo(atmBalanceAfter);
    }

    /**
     * общая умма всех банкнот во всех пачках
     *
     * @param banknotes - банкноты по пачкам
     * @return сумма
     */
    int banknotesSum(List<List<Banknote>> banknotes) {
        return Optional.ofNullable(banknotes
                .stream()
                .map(l -> l
                        .stream()
                        .map(Banknote::getNominal)
                        .reduce(Integer::sum).orElse(0))
                .reduce(Integer::sum).orElse(0)).orElse(0);
    }

    /**
     * Проверим, а в каждой ли пачке банкноты одного номинала
     * @param banknotes - список пачек бонкнот
     * @return - результат проверки
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
