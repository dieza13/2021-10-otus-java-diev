package ru.otus.d13.aop;

import ru.otus.d13.aop.proxy.loglib.factories.LoggableClassFactory;
import ru.otus.d13.aop.someclasses.SomeInterface;


public class MainClass {

    public static void main(String[] args) {
        try {
            SomeInterface inst = LoggableClassFactory.createImplWithLoggingPrms(SomeInterface.class);
            System.out.println("<---First call:");
            inst.returnSomething(10d);
            System.out.println("<---Second call:");
            inst.returnSomething(24d,45d);
            System.out.println("<---Third call:");
            inst.showSomething(34,"piggy",48.45);
            System.out.println("<---Fourth call:");
            inst.showSomething(3,33d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
