package persistence.sql.ddl;

import persistence.entity.CustomJoinTable;

public abstract class SelectQueryBuilder {

    public String findAll(String tableName) {
        return String.format("select * from %s", tableName);
    }

    public String findById(String tableName, String columnName, String columnValue) {
        return String.format("select * from %s where %s=%s", tableName, columnName, columnValue);
    }


    public String find(String tableName) {
        return String.format("select * from %s", tableName);
    }

    public String appendWhere(String sql, String columnName, String columnValue) {
        return String.format("%s where %s=%s", sql, columnName, columnValue);
    }

    public String appendJoin(String sql, CustomJoinTable customJoinTable) {
        return sql + String.format(" join %s on %s ",
                customJoinTable.joinTable(),
                customJoinTable.joinColumn()
        );
    }
}
