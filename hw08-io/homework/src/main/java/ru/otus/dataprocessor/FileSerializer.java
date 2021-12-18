package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.*;
import java.util.Map;

public class FileSerializer implements Serializer {

    private final String fileName;
    JsonMapper jsonMapper = new JsonMapper();

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        //формирует результирующий json и сохраняет его в файл
        try (var bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
            bufferedWriter.write(jsonMapper.writeValueAsString(data));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка сериализации данных: ",e);
        }
    }
}
