package ru.otus.d13.reflection.test;


import ru.otus.d13.reflection.someclasses.PositiveDivider;
import ru.otus.d13.reflection.testfw.annotations.After;
import ru.otus.d13.reflection.testfw.annotations.Before;
import ru.otus.d13.reflection.testfw.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PositiveDividerTest {

    private List listDivider = new ArrayList() {{
        add(null);
        add("one");
        add(-1d);
        add(0d);
        add(2.2d);
        add(10d);
        add(11d);
        add(12d);
    }};
    private List listDenom = new ArrayList() {{
        add(2d);
        add(3d);
        add(4d);
        add(5d);
        add(-1d);
        add(null);
        add(10d);
        add(15d);
    }};
    private static AtomicInteger testNum = new AtomicInteger(-1);
    private PositiveDivider positiveDivider;

    @Before
    public void setUp() {
        this.positiveDivider = new PositiveDivider();
        System.out.println("Before test");
        Double divider = (Double) listDivider.get(testNum.incrementAndGet());
        positiveDivider.setDivider(divider);
    }

    @Test
    public void test1() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test2() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test3() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test4() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test5() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test6() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test7() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    @Test
    public void test8() {
        executeTest((Double)listDenom.get(testNum.get()));
    }

    private void executeTest(double denom) {
        double res = positiveDivider.divideMe(denom);
        System.out.printf("%f / %f = %f%n",denom , positiveDivider.getDivider(), res);
    }

    @After
    public void tearDown() {
        System.out.println("After test");
        Optional.ofNullable(positiveDivider).orElse(new PositiveDivider()).clearDivider();
    }

}