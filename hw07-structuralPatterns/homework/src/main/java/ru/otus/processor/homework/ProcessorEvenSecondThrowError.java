package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

import java.time.LocalDateTime;

public class ProcessorEvenSecondThrowError implements Processor {

    private final LocalDateTimeProvider localDateTimeProvider;

    public ProcessorEvenSecondThrowError(LocalDateTimeProvider localDateTimeProvider) {
        this.localDateTimeProvider = localDateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        LocalDateTime ld = localDateTimeProvider.getDateTime();
        if (localDateTimeProvider.getDateTime().getSecond() % 2 == 0) {
            throw new RuntimeException("Я работаю только по четным секундам месяца");
        }
        return message.toBuilder().build();
    }
}
