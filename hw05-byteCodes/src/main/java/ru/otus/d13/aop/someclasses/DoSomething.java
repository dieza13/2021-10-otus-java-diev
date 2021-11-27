package ru.otus.d13.aop.someclasses;

import ru.otus.d13.aop.proxy.loglib.annotations.Log;

public class DoSomething implements SomeInterface{

    @Log
    public void showSomething(int iParam, String sParam, double dParam) {
        System.out.println("Look at my in params: " + iParam + ", " + sParam + ", " + dParam);
    }

    @Log
    public void showSomething(int iParam, double dParam) {
        System.out.println("You give me 2 params: " + iParam + ", " + dParam);
    }

    @Log
    public double returnSomething(double dParam) {
        System.out.println("You give me: " + (dParam+=30));
        return dParam;
    }

    public double returnSomething(double dParam1, double dParam2) {
        System.out.println("You give me: " + dParam1 + " and " + dParam2);
        return (dParam1 + dParam2) / (dParam1 * dParam2);
    }

}
