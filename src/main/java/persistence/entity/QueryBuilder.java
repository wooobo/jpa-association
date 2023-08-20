package persistence.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.CustomTable;
import persistence.EntityMeta;
import persistence.sql.ddl.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class QueryBuilder {
    private final SelectQueryBuilder selectQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    protected QueryBuilder(SelectQueryBuilder selectQueryBuilder, DeleteQueryBuilder deleteQueryBuilder, InsertQueryBuilder insertQueryBuilder, UpdateQueryBuilder updateQueryBuilder, JdbcTemplate jdbcTemplate) {
        this.selectQueryBuilder = selectQueryBuilder;
        this.deleteQueryBuilder = deleteQueryBuilder;
        this.insertQueryBuilder = insertQueryBuilder;
        this.updateQueryBuilder = updateQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    protected <T> Object findById(Class<T> clazz, Long key) {
        String sql = selectQueryBuilder.findById(CustomTable.of(clazz).name(), unique(clazz.getDeclaredFields()).getName(), String.valueOf(key));
        EntityLoader<T> mapper = new EntityLoader<>(clazz);

        return jdbcTemplate.queryForObject(sql, mapper);
    }

    // @Todo 여기서 proxy 객체를 만들어서 반환해야 한다?
    public <T> Object findByIdJoin(Class<T> clazz, Long key) {
        EntityMeta entityMeta = EntityMeta.ofJoin(clazz);
        String sql = selectQueryBuilder.findByIdByJoin(entityMeta, String.valueOf(key));
        EntityLoader<T> mapper = new EntityLoader<>(clazz);

        return jdbcTemplate.queryForObject(sql, mapper);
    }

    protected <T> void delete(Class<T> clazz, Long key) {
        jdbcTemplate.execute(deleteQueryBuilder.delete(CustomTable.of(clazz).name(), unique(clazz.getDeclaredFields()).getName(), key.toString()));
    }

    public void save(Object entity) {
        insertQueryBuilder.createInsertBuild(entity);
    }

    public void update(Long key, String tableName, Object entity) {
        jdbcTemplate.execute(updateQueryBuilder.createUpdateBuild(key, tableName, ColumnMap.of(entity)));
    }

    private Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public <T> List<T> findAllBy(Object parentClazz, Field joinField) {
        Field uniqueField = unique(parentClazz.getClass().getFields());
        EntityMeta entityMeta = EntityMeta.ofJoin(joinField.getType());
        String sql = null;
        try {
            sql = selectQueryBuilder.findAllChildBy(entityMeta, uniqueField.get(parentClazz).toString());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        EntityLoader<?> mapper = new EntityLoader<>(joinField.getType());

        jdbcTemplate.query(sql, mapper);

        return null;
    }
}
