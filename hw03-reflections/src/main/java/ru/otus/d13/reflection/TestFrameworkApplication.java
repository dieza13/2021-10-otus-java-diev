package ru.otus.d13.reflection;

import ru.otus.d13.reflection.test.PositiveDividerTest;
import ru.otus.d13.reflection.testfw.testfwexecutors.TestsExecutor;

public class TestFrameworkApplication {

    public static void main(String[] args) {
        TestsExecutor.testClassExecute(PositiveDividerTest.class);
    }
}
