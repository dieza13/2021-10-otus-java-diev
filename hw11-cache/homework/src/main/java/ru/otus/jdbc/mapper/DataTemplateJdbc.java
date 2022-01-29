package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.crm.model.Client;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return (T)getObjectFromResultSet(rs);
                }
                return null;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
            var objectList = new ArrayList<T>();
            try {
                while (rs.next()) {
                    objectList.add((T)getObjectFromResultSet(rs));
                }
                return objectList;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        }).orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        try {
            return dbExecutor.executeStatement(connection,entitySQLMetaData.getInsertSql() , getClassParamValues(client));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            List<Object> values = getClassParamValues(client);
            values.add(getParamValue(client,entityClassMetaData.getIdField()));
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), values);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getObjectFromResultSet(ResultSet rs) {
        try {
            final Object obj = entityClassMetaData.getConstructor().newInstance();
            List<Field> flds = entityClassMetaData.getAllFields();
            flds.forEach(fld -> {
                try {
                    fld.setAccessible(true);
                    fld.set(obj, rs.getObject(fld.getName()));
                } catch (Exception e) {
                    throw new DataTemplateException(e);
                }
            });
            return (T) obj;
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> getClassParamValues(Object obj) {
        List<Field> fields = entityClassMetaData.getFieldsWithoutId();
        return  fields.stream().map(fld-> getParamValue(obj,fld)).collect(Collectors.toList());
    }

    private Object getParamValue(Object obj, Field fld) {
        try {
            fld.setAccessible(true);
            return fld.get(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
