package ru.otus.d13.reflection.testfw.testfwexecutors;

import ru.otus.d13.reflection.testfw.annotations.After;
import ru.otus.d13.reflection.testfw.annotations.Before;
import ru.otus.d13.reflection.testfw.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TestsExecutor {

    public static void testClassExecute(Class testClass) {
        if (Optional.ofNullable(testClass).isEmpty()) {
            return;
        }
        System.out.printf("<---\"%s\" class testing start:%n", testClass.getCanonicalName());

        final ArrayList<Method> beforeMtds = new ArrayList<>();
        final ArrayList<Method> afterMtds = new ArrayList<>();
        final ArrayList<Method> testMtds = new ArrayList<>();
        final int allCnt,errCnt,sucCnt;
        final int[] resCnt = {0,0,0};
        /*
          Получаем списки методов: @Test,@Before,@After
         */
        Optional.of(testClass.getMethods())
                .ifPresentOrElse(mtdsExists-> Arrays.stream(mtdsExists).forEach(m->{
                    if (m.getAnnotationsByType(Test.class).length > 0) {
                        testMtds.add(m);
                        resCnt[0]++;
                    } else if (m.getAnnotationsByType(After.class).length > 0) {
                        afterMtds.add(m);
                    } else if(m.getAnnotationsByType(Before.class).length > 0) {
                        beforeMtds.add(m);
                    }
                }),()-> System.out.printf(">---no test methods defined in class \"%s\"", testClass.getCanonicalName()));

        /*
          последовательно вызываем  @Before[],@Test,@After[] для каждого @Test
         */
        testMtds.forEach(method -> {
            System.out.printf("<--Start executing test method \"%s\":%n", method.getName());

            Object testClassInst = null;
            try {
                testClassInst = testClass.getDeclaredConstructor().newInstance();
                for (Method m : beforeMtds) {
                    m.invoke(testClassInst);
                }
                method.invoke(testClassInst);
                resCnt[1]++;
            } catch(Exception e) {
                System.out.printf("--Test method \"%s\" exception:%n", method.getName());
//                e.printStackTrace();
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                System.out.println(sw);

                resCnt[2]++;
            } finally {
                for (Method m : afterMtds) {
                    try {
                        m.invoke(testClassInst);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }


            System.out.printf(">--Test method \"%s\" finished.%n", method.getName());
        });
        System.out.printf("\n\"%s\" class contains %d tests, %d succeed, %d failed%n", testClass.getCanonicalName(),resCnt[0],resCnt[1],resCnt[2]);
        System.out.printf(">---\"%s\" class finished all tests%n", testClass.getCanonicalName());
    }
}
