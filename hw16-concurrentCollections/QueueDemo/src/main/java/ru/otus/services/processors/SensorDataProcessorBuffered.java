package ru.otus.services.processors;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.lib.SensorDataBufferedWriter;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

// Этот класс нужно реализовать
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final ArrayBlockingQueue<SensorData> bufferedData;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
         bufferedData = new ArrayBlockingQueue<>(bufferSize);
    }

    @SneakyThrows
    @Override
    public void process(SensorData data) {

        if (bufferedData.size() >= bufferSize) {
            flush();
        }
        bufferedData.put(data);
    }

    public void flush() {
        try {
            synchronized (bufferedData) {
                List<SensorData> list = null;
                if (bufferedData.size() == 0) {
                    return;
                }
                list = bufferedData.stream().sorted(Comparator.comparing(SensorData::getMeasurementTime)).collect(Collectors.toList());
                bufferedData.clear();
                writer.writeBufferedData(list);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }
}
