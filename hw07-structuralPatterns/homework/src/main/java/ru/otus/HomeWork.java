package ru.otus;

import ru.otus.handler.ComplexProcessor;
import ru.otus.listener.ListenerPrinterConsole;
import ru.otus.listener.homework.HistoryListener;
import ru.otus.model.Message;
import ru.otus.processor.LoggerProcessor;
import ru.otus.processor.Processor;
import ru.otus.processor.ProcessorConcatFields;
import ru.otus.processor.ProcessorUpperField10;
import ru.otus.processor.homework.ProcessorEvenSecondThrowError;
import ru.otus.processor.homework.ProcessorField11SwapField12;

import java.time.LocalDateTime;
import java.util.List;

public class HomeWork {

    /*
     Реализовать to do:
       1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
       2. Сделать процессор, который поменяет местами значения field11 и field12
       3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
             Секунда должна определяьться во время выполнения.
             Тест - важная часть задания
             Обязательно посмотрите пример к паттерну Мементо!
       4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
          Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
          Для него уже есть тест, убедитесь, что тест проходит
     */

    public static void main(String[] args) {
        /*
           по аналогии с Demo.class
           из элеменов "to do" создать new ComplexProcessor и обработать сообщение
         */

        var processors1 = List.of(
                new ProcessorField11SwapField12(),
                new ProcessorEvenSecondThrowError(()-> LocalDateTime.of(2021,1,1,1,1,1))
                );

        var complexProcessor1 = new ComplexProcessor(processors1, ex -> {});
        var historyListener = new HistoryListener();
        complexProcessor1.addListener(historyListener);

        var message1 = new Message.Builder(13L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .build();
        var message2 = new Message.Builder(14L).build();

        System.out.println("MSG before handling with swap: " + message1);
        var result = complexProcessor1.handle(message1);
        System.out.println("MSG after handling with swap: " + result);

        System.out.println("Handle next MSG: " + message2);
        complexProcessor1.handle(message2);
        var histMsg13 = historyListener.findMessageById(13);
        var histMsg14 = historyListener.findMessageById(14);
        System.out.println("Get history by id 13: " + histMsg13);
        System.out.println("Get history by id 14: " + histMsg14);
        complexProcessor1.removeListener(historyListener);

        System.out.println("Handle MSG on even sec: " + message2);
        var processors2 = List.of(
                (Processor)new ProcessorEvenSecondThrowError(()-> LocalDateTime.of(2021,2,2,2,2,2))
        );
        var complexProcessor2 = new ComplexProcessor(processors2, ex -> {throw new RuntimeException(ex);});
        try {
            complexProcessor2.handle(message2);
        } catch (Exception e) {
            System.out.println("Error on MSG handling: " + e.getMessage());
        }

    }
}
