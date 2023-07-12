package persistence.entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

public class CustomJoinTable {
    private final String rootTable;
    private final String joinTable;
    private final UniqueColumn rootColumn;
    private final UniqueColumn joinColumn;

    public CustomJoinTable(String rootTable, String joinTable, UniqueColumn rootColumn, UniqueColumn joinColumn) {
        this.rootTable = rootTable;
        this.joinTable = joinTable;
        this.rootColumn = rootColumn;
        this.joinColumn = joinColumn;
    }

    public static <T> CustomJoinTable of(Class<T> clazz) {
        Table table = findTableFiled(clazz);

        assert table != null;
        return new CustomJoinTable(
                clazz.getSimpleName(),
                getJoinTable(table),
                UniqueColumn.of(clazz), // 유니크 컬럼이름 , unique 함수있어야겠다
                UniqueColumn.of(clazz)
        );
    }

    public static String getJoinTable(Table table) {
        return table.name();
    }

    private static <T> Table findTableFiled(Class<T> clazz) {
        Optional<Field> joinField = getJoinField(clazz);

        if (joinField.isEmpty()) {
            return null;
        }

        ParameterizedType genericType = (ParameterizedType) joinField.get().getGenericType();

        Class<?> fieldClass = (Class<?>) genericType.getActualTypeArguments()[0];
        return fieldClass.getAnnotation(Table.class);
    }

    private static <T> Optional<Field> getJoinField(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(it -> it.isAnnotationPresent(JoinColumn.class))
                .findFirst();
    }

    public boolean hasJoin() {
        return joinTable != null;
    }

    public String joinTable() {
        return joinTable;
    }

    public String joinColumn() {
        return joinColumn.toOn(joinTable) + "=" + rootColumn.toOn(rootTable);
    }
}
