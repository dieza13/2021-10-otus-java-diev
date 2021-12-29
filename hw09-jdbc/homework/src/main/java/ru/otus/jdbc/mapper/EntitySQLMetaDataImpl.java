package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData{

    private enum QUERIES{
        selectAll("select %s from %s"),
        selectById("select %s from %s where %s = ?"),
        insert("insert into %2$2s(%1$2s) values (%3$2s)"),
        update("update %2$2s set %1$2s where %3$2s = ?");

        private String query;

        QUERIES( String query) {
            this.query = query;
        }
    }

    private final EntityClassMetaData entityClassMetaData;
    private final HashMap<String,String> queries = new HashMap<>();
    private String tableName;

    public EntitySQLMetaDataImpl(EntityClassMetaData entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
        tableName = entityClassMetaData.getConstructor().getDeclaringClass().getSimpleName().toLowerCase();
        List<String> allFields = fieldsAsStringsList(entityClassMetaData.getAllFields());
        List<String> fields = fieldsAsStringsList(entityClassMetaData.getFieldsWithoutId());
        String selectAll =
                getQuery(QUERIES.selectAll.query
                        ,allFields
                        ,Collections.emptyList()
                );
        String selectById =
                getQuery(QUERIES.selectById.query
                        ,allFields
                        ,List.of(entityClassMetaData.getIdField().getName())
                );
        String insert =
                getQuery(QUERIES.insert.query
                        ,fields
                        ,List.of(String.join(", ", Collections.nCopies(entityClassMetaData.getFieldsWithoutId().size(), "?")))
                );
        String update =
                getQuery(QUERIES.update.query
                        ,fields.stream().map(f->f.concat(" = ?")).collect(Collectors.toList())
                        ,List.of(entityClassMetaData.getIdField().getName())
                );

        queries.put(QUERIES.selectAll.name(),selectAll);
        queries.put(QUERIES.selectById.name(),selectById);
        queries.put(QUERIES.insert.name(),insert);
        queries.put(QUERIES.update.name(),update);
        System.out.println();
    }

    private List<String> fieldsAsStringsList(List<Field> fields) {
        return fields.stream().map(Field::getName).collect(Collectors.toList());
    }

    private String getQuery(String select, List<String> flds, List<Object> args) {
        String columns = String.join(", ",flds);
        List<Object> resArgs = new ArrayList<>(){
            {
                add(columns);
                add(tableName);
                addAll(args);
            }};
        return Utils.getSQLText(select,resArgs.toArray());
    }

    @Override
    public String getSelectAllSql() {
        return queries.get(QUERIES.selectAll.name());
    }

    @Override
    public String getSelectByIdSql() {
        return queries.get(QUERIES.selectById.name());
    }

    @Override
    public String getInsertSql() {
        return queries.get(QUERIES.insert.name());
    }

    @Override
    public String getUpdateSql() {
        return queries.get(QUERIES.update.name());
    }
}
