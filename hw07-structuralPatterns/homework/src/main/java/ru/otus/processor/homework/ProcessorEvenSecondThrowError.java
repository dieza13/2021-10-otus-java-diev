package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;


public class ProcessorEvenSecondThrowError implements Processor {

    private final LocalDateTimeProvider localDateTimeProvider;

    public ProcessorEvenSecondThrowError(LocalDateTimeProvider localDateTimeProvider) {
        this.localDateTimeProvider = localDateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        if (localDateTimeProvider.getDateTime().getSecond() % 2 == 0) {
            throw new RuntimeException("Я работаю только по четным секундам месяца");
        }
        return message.toBuilder().build();
    }
}
