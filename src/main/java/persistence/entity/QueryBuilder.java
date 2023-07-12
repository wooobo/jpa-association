package persistence.entity;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.*;

import java.lang.reflect.Field;
import java.util.Arrays;

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
        EntityLoader<T> entityLoader = new EntityLoader<>(clazz);
        if (!entityLoader.hasJoin()) {
            // join이 아닐때,
            String sql = selectQueryBuilder.findById(clazz.getSimpleName(), unique(clazz.getDeclaredFields()).getName(), String.valueOf(key));
            return jdbcTemplate.queryForObject(sql, entityLoader);
        }

        // join 일때
        String sql = selectQueryBuilder.find(clazz.getSimpleName());
        String sqlJoin = selectQueryBuilder.appendJoin(sql, entityLoader.customJoinTable());
        String finalSql = selectQueryBuilder.appendWhere(sqlJoin, unique(clazz.getDeclaredFields()).getName(), String.valueOf(key));

        return jdbcTemplate.queryForObject(finalSql, entityLoader);
    }

    protected <T> void delete(Class<T> clazz, Long key) {
        jdbcTemplate.execute(deleteQueryBuilder.delete(clazz.getSimpleName(), unique(clazz.getDeclaredFields()).getName(), key.toString()));
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

}
