package persistence.sql.ddl;

import jakarta.persistence.Table;

public abstract class InsertQueryBuilder {

    protected InsertQueryBuilder() {
    }

    public String createInsertBuild(Object object) {
        ColumnMap columnMap = ColumnMap.of(object);
        return String.format("insert into %s (%s) values (%s)", tableName(object.getClass()), columnMap.names(), columnMap.values());
    }

    private String tableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            return clazz.getSimpleName().toLowerCase();
        }
        
        return table.name();
    }
}
