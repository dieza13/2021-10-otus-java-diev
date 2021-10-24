package ru.otus.d13.reflection.someclasses;

import java.util.Optional;

public class PositiveDivider {
    /**
     * I can divide only 2 positive double.
     * I apologize for the inconvenience
     */
    private Double divider;

    /**
     * Установим делитель (положительный)
     * @param divider - делитель
     */
    public void setDivider(Double divider) {

        if (divider == null || divider <= 0) {
            throw new DividerInitException(String.format("Я могу только положительные числа делить((, делить на %f не умею",divider));
        } else {
            this.divider = divider;
        }
    }

    public Double getDivider() {
        return divider;
    }

    /**
     * Делит denominator на установленный заранее через setDivider знаменатель
     * @param denominator - знаменатель
     * @return - результат деления
     */
    public Double divideMe(Double denominator) {
        if (divider == null){
            throw new DividerInitException("Делитель установите сначала, пожалуйста!");
        } else if (denominator == null || divider <= 0) {
            throw new DividerInitException(String.format("Я могу только положительные числа делить((, делить %f на %f не умею", denominator, divider));
        }
        return denominator / divider;
    }

    /**
     * ОбNULLаем знаменатель
     */
    public void clearDivider() {
        divider = null;
    }
}
