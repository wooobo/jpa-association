package persistence.entity;

import jdbc.RowMapper;
import persistence.EntityMeta;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class EntityLoader<T> implements RowMapper<T> {
    private final Class<T> targetType;
    private final EntityMeta entityMeta;
    private final CustomJoinTable joinTable;

    public EntityLoader(Class<T> targetType) {
        this.targetType = targetType;
        this.entityMeta = initEntityMeta(targetType);
        this.joinTable = initJoinTable(targetType);
    }

    private CustomJoinTable initJoinTable(Class<T> targetType) {
        return CustomJoinTable.of(targetType);
    }

    private EntityMeta initEntityMeta(Class<T> targetType) {
        return EntityMeta.of(targetType);
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();
        T targetObject = null;
        try {
            targetObject = targetType.getDeclaredConstructor().newInstance();
            
            for (int i = 0; i < columnCount; i++) {
                String alias = meta.getColumnLabel(i + 1);
                String name = meta.getColumnName(i + 1);
                Field field = targetType.getDeclaredField(entityMeta.column(alias));
                field.setAccessible(true);

                Object value = resultSet.getObject(name);
                field.set(targetObject, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetObject;
    }

    public boolean hasJoin() {
        return this.joinTable.hasJoin();
    }

    public CustomJoinTable customJoinTable() {
        return joinTable;
    }
}
