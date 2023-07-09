package persistence;

import domain.Person;
import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityMeta {
    private final Map<String, String> columnMap;

    private EntityMeta(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    public static EntityMeta of(Class<?> clazz) {
        return new EntityMeta(Arrays.stream(clazz.getDeclaredFields())
                .collect(Collectors.toMap(EntityMeta::columnName, Field::getName)));
    }

    private static String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        if (columnAnnotation == null) {
            return field.getName();
        }

        if (columnAnnotation.name().equals("")) {
            return field.getName();
        }

        return columnAnnotation.name();
    }

    public String column(String alias) {
        return columnMap.get(alias.toLowerCase());
    }
}
