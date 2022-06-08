package com.example.reweb.ulti;

import com.example.reweb.annotation.Column;
import com.example.reweb.annotation.ForeignKey;
import com.example.reweb.annotation.Id;
import com.example.reweb.annotation.Table;
import com.example.reweb.ulti.Config.Config;
import com.example.reweb.ulti.Config.ConfigSql;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.Set;

public class Migration {
    private static Connection connection;
    public static final String scanFolder = "com.example.reweb";

    static {
        try {
            connection = ConnectionHelper.createConnection();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Set<Class<?>> annotated = getAnnotation();
        for (Class<?> table : annotated
        ) {
            createTable(table);
        }
//        dropAllTable();
    }

    public static boolean isExistsTable(String tableName) {
        try {
            String checkSql = ConfigSql.SELECT +
                    ConfigSql.SPACE +
                    ConfigSql.COUNT +
                    ConfigSql.OPEN_BRACKET +
                    ConfigSql.STAR +
                    ConfigSql.CLOSE_BRACKET +
                    ConfigSql.SPACE +
                    ConfigSql.FROM +
                    ConfigSql.SPACE +
                    ConfigSql.INFORMATION_SCHEMA +
                    ConfigSql.SPACE +
                    ConfigSql.WHERE +
                    ConfigSql.SPACE +
                    ConfigSql.TABLE_SCHEMA +
                    ConfigSql.SPACE +
                    ConfigSql.EQUAL_SIGN +
                    ConfigSql.SPACE +
                    ConfigSql.QUESTION +
                    ConfigSql.SPACE +
                    ConfigSql.AND +
                    ConfigSql.SPACE +
                    ConfigSql.TABLE_NAME +
                    ConfigSql.EQUAL_SIGN +
                    ConfigSql.SPACE +
                    ConfigSql.QUESTION;
            PreparedStatement stt = connection.prepareStatement(checkSql);
            stt.setString(1, Config.DB_NAME);
            stt.setString(2, tableName);
            ResultSet resultSet = stt.executeQuery();

            if (resultSet.next()) {
                if (resultSet.getInt(1) > 0)
                    return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static void dropAllTable() {
        Set<Class<?>> annotated = getAnnotation();
        for (Class<?> table : annotated
        ) {
            dropForeignKey(table);

        }
        for (Class<?> table : annotated
        ) {
            dropTable(table);

        }
    }

    public static void dropForeignKey(Class clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return;
        }
        String tableName = clazz.getSimpleName().toLowerCase() + "s";

        Table table = (Table) clazz.getDeclaredAnnotation(Table.class);
        if (table.name().length() > 0) {
            tableName = table.name();
        }
        Field[] listField = clazz.getDeclaredFields();
        for (Field field : listField) {
            if (!field.isAnnotationPresent(ForeignKey.class)) {
                continue;
            }
            ForeignKey foreignKey = field.getDeclaredAnnotation(ForeignKey.class);
            String dropColumnKey = ConfigSql.ALTER_TABLE + ConfigSql.SPACE + tableName + ConfigSql.SPACE + ConfigSql.DROP + ConfigSql.SPACE + ConfigSql.FOREIGN_KEY + ConfigSql.SPACE + getConstraintName(foreignKey, tableName);
            System.out.println(dropColumnKey);
            try {
                PreparedStatement stt = connection.prepareStatement(dropColumnKey);
                stt.execute();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void dropTable(Class clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return;
        }
        StringBuilder sqlString = new StringBuilder();
        sqlString.append(ConfigSql.CREATE_TABLE);
        sqlString.append(ConfigSql.SPACE);
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        Table table = (Table) clazz.getDeclaredAnnotation(Table.class);
        if (table.name().length() > 0) {
            tableName = table.name();
        }
        if (!isExistsTable(tableName)) {
            return;
        }
        try {
            Statement DropStt = connection.createStatement();
            DropStt.execute(String.format(ConfigSql.DROP_TABLE + " %s", tableName));
            System.out.printf("Delete table %s success\n", tableName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void createTable(Class clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return;
        }
        StringBuilder sqlString = new StringBuilder();
        sqlString.append(ConfigSql.CREATE_TABLE);
        sqlString.append(ConfigSql.SPACE);
        String tableName = clazz.getSimpleName().toLowerCase() + "s";
        Table table = (Table) clazz.getDeclaredAnnotation(Table.class);
        if (table.name().length() > 0) {
            tableName = table.name();
        }
        if (isExistsTable(tableName)) {
            System.out.println("OUT");
            return;
        }
        sqlString.append(tableName);
        sqlString.append(ConfigSql.SPACE);
        sqlString.append(ConfigSql.OPEN_BRACKET);
        Field[] listField = clazz.getDeclaredFields();
        for (Field field : listField
        ) {
            String columnName = field.getName();
            String columnType = "";
            if (field.getType().getSimpleName().contains("String")) {
                columnType = "VARCHAR(250)";
            } else {
                columnType = field.getType().getSimpleName();
            }
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getDeclaredAnnotation(Column.class);
                columnName = column.name();
                columnType = column.type();
                sqlString.append(columnName);
                sqlString.append(ConfigSql.SPACE);
//            sqlString.append(field.getType().getSimpleName());
                sqlString.append(columnType);
                sqlString.append(ConfigSql.SPACE);
                if (field.isAnnotationPresent(Id.class)) {
                    Id id = field.getDeclaredAnnotation(Id.class);
                    sqlString.append(ConfigSql.PRIMARY_KEY);
                    sqlString.append(ConfigSql.SPACE);
                    if (id.AutoIncrement()) {
                        sqlString.append(ConfigSql.AUTO_INCREMENT);
                        sqlString.append(ConfigSql.SPACE);
                    }
                }
                if (field.isAnnotationPresent(ForeignKey.class)) {
                    ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
                    // check có bảng hay chưa
                    if (!isExistsTable(foreignKey.referenceTable())) {
                        Set<Class<?>> annotated = getAnnotation();
                        for (Class<?> preTable : annotated
                        ) {
                            Table preTableName = preTable.getDeclaredAnnotation(Table.class);
                            if (preTableName.name().equals(foreignKey.referenceTable())) {
                                System.out.println("Create");
                                createTable(preTable);
                            }
                        }
                    }
                    String constraintName = getConstraintName(foreignKey, tableName);

                    //end
                    sqlString.append(ConfigSql.COMMA);
                    sqlString.append(ConfigSql.CONSTRAINT);
                    sqlString.append(ConfigSql.SPACE);
                    sqlString.append(constraintName);
                    sqlString.append(ConfigSql.SPACE);
                    sqlString.append(ConfigSql.FOREIGN_KEY);
                    sqlString.append(ConfigSql.SPACE);
                    sqlString.append(ConfigSql.OPEN_BRACKET);
                    sqlString.append(columnName);
                    sqlString.append(ConfigSql.CLOSE_BRACKET);
                    sqlString.append(ConfigSql.SPACE);
                    sqlString.append(ConfigSql.REFERENCES);
                    sqlString.append(ConfigSql.SPACE);
                    sqlString.append(foreignKey.referenceTable());
                    sqlString.append(ConfigSql.OPEN_BRACKET);
                    sqlString.append(foreignKey.referenceColumn());
                    sqlString.append(ConfigSql.CLOSE_BRACKET);
                }
                sqlString.append(ConfigSql.COMMA);
            }

        }
        sqlString.setLength(sqlString.length() - 1);
        sqlString.append(ConfigSql.CLOSE_BRACKET);
        System.out.println(sqlString);
        try {
            PreparedStatement statement = connection.prepareStatement(sqlString.toString());
            statement.execute();
            System.out.printf(ConfigSql.CREATE_TABLE + " %s success%n", tableName);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static String getConstraintName(ForeignKey foreignKey, String tableName) {
        if (foreignKey.constraint().length() > 0) {
            return foreignKey.constraint();
        } else {
            return "fk_" + tableName + '_' + foreignKey.referenceTable();
        }
    }

    private static Set<Class<?>> getAnnotation() {
        Reflections reflections = new Reflections(scanFolder);
        return reflections.getTypesAnnotatedWith(Table.class);
    }

}
