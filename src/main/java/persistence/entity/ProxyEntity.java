package persistence.entity;

import domain.OrderItem;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ProxyEntity {
    private final String targetFieldName;
    private final Object parentObject;
    private final QueryBuilder queryBuilder;

    public ProxyEntity(String targetFieldName, Object parentObject, QueryBuilder queryBuilder) {
        this.targetFieldName = targetFieldName;
        this.parentObject = parentObject;
        this.queryBuilder = queryBuilder;
    }

    public static Object createProxy(String targetFieldName, Object parentObject, QueryBuilder queryBuilder) throws NoSuchFieldException, IllegalAccessException {
        Field targetField = parentObject.getClass().getDeclaredField(targetFieldName);
        targetField.setAccessible(true);
        
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(List.class);
        enhancer.setCallback((LazyLoader) () -> {
            try {
                System.out.println("load");
//                return queryBuilder.findAllBy(parentObject.getClass(), targetField);
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(new OrderItem("ok1", 1, 1));
                return orderItems;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        targetField.set(parentObject, enhancer.create());

        return parentObject;
    }
}
