package persistence.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class CustomJoinTable {
    private final String rootTable;
    private final String joinTable;
    private final CustomColumn rootColumn;
    private final CustomColumn joinColumn;

    public CustomJoinTable(String rootTable, String joinTable, CustomColumn rootColumn, CustomColumn joinColumn) {
        this.rootTable = rootTable;
        this.joinTable = joinTable;
        this.rootColumn = rootColumn;
        this.joinColumn = joinColumn;
    }

    public static <T> CustomJoinTable of(Class<T> clazz) {
        return new CustomJoinTable(
                clazz.getSimpleName(),
                getJoinTable(clazz),
                CustomColumn.of(),
                CustomColumn.of()
        );
    }

    public static <T> String getJoinTable(Class<T> clazz) {
        Field joinField = Arrays.stream(clazz.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(JoinColumn.class))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        ParameterizedType genericType = (ParameterizedType) joinField.getGenericType();

        Class<?> fieldClass = (Class<?>) genericType.getActualTypeArguments()[0];
        Table table = fieldClass.getAnnotation(Table.class);
        return table.name();
    }
}

//        JoinColumn joinColumnAnnotation = joinField.getAnnotation(JoinColumn.class);
//        return joinColumnAnnotation.name();
