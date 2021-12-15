package ru.otus.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.homework.ProcessorEvenSecondThrowError;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorEvenSecondThrowErrorTest {

    @Test
    @DisplayName("Тестируем нормальный вызов процессора")
    void handleProcessorsTest() {
        Processor processor = new ProcessorEvenSecondThrowError(()->LocalDateTime.of(2021,1,1,1,1,1));
        Message message = processor.process(new Message.Builder(1).field1("сработало").build());
        Message resMessage = processor.process(message);
        assertThat(resMessage.getField1()).isEqualTo("сработало");
    }

    @Test
    @DisplayName("Тестируем генерацию исключения на четных секундах")
    void handleExceptionTest() {
        Message message = new Message.Builder(1).build();
        for (int i = 1; i < 6; i++) {
            try {
                final int sec = i * 2;
                Processor processor = new ProcessorEvenSecondThrowError(()->LocalDateTime.of(2021,1,1,1,1,sec));
                processor.process(message);
                throw new RuntimeException("Не происходит генерации исключения на четных секундах");
            } catch (Exception e) {
                assertThat(e.getMessage()).isEqualTo("Я работаю только по четным секундам месяца");
            }
        }
    }
}
