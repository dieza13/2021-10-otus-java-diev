package ru.otus.jdbc.mapper;

public class Utils {
    public static String getErrText(String err, Object ... args) {
        return String.format(err,args);
    }
    public static String getSQLText(String sql, Object ... args) {
        return String.format(sql,args);
    }
}
