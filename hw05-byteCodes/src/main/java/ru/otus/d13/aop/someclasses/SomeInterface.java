package ru.otus.d13.aop.someclasses;

public interface SomeInterface {
    void showSomething(int iParam, String sParam, double dParam);
    void showSomething(int iParam, double dParam);
    double returnSomething(double dParam);
    double returnSomething(double dParam1, double dParam2);
}
