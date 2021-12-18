package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import ru.otus.model.Measurement;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourcesFileLoader implements Loader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String fileName;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        //читает файл, парсит и возвращает результат
        try (FileInputStream fis = new FileInputStream(new File(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI()))){
            return Lists.newArrayList(objectMapper.readTree(fis).elements())
                    .stream()
                    .map(n-> new Measurement(n.get("name").asText(),n.get("value").doubleValue()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка десериализации данных: ",e);
        }
    }
}
