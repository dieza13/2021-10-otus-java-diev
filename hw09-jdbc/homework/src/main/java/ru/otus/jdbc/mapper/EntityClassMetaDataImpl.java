package ru.otus.jdbc.mapper;

import ru.otus.core.persistence.Id;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T>{

    private final static String ERR_NO_ARGS_CONSTRUCTOR ="У класса %s отсутствует конструктор без параметров";
    private final static String ERR_NO_ID_PRESENT = "Для класса %s не задано ключевое поле";
    private final static String ERR_TOO_MATCH_ID_PRESENT = "Для класса %s определено больше одного идентификатора";

    private final String name;
    private final Constructor<T> constructor;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;
    private final Field idField;

    public  EntityClassMetaDataImpl(Class<T> clazz) {

        name = clazz.getCanonicalName();
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ClassMetaDataException(Utils.getErrText(ERR_NO_ARGS_CONSTRUCTOR,name),e);
        }

        allFields = Arrays.asList(clazz.getDeclaredFields());
        Optional.of(allFields.stream().filter(f->f.isAnnotationPresent(Id.class)).count()).ifPresent(cnt->{
            if (cnt == 0) {
                throw new ClassMetaDataException(Utils.getErrText(ERR_NO_ID_PRESENT,name));
            }
            if (cnt > 1) {
                throw new ClassMetaDataException(Utils.getErrText(ERR_TOO_MATCH_ID_PRESENT,name));
            }
        });

        idField = allFields.stream().filter(f->f.isAnnotationPresent(Id.class)).findFirst().get();
        fieldsWithoutId = allFields.stream().filter(f->!f.isAnnotationPresent(Id.class)).collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}
