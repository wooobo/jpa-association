package CGLib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

import java.lang.reflect.Field;

public class LazyLoadingInterceptor implements LazyLoader {
    private final String fieldName;
    private final Object targetObject;

    public LazyLoadingInterceptor(String fieldName, Field targetObject) {
        this.fieldName = fieldName;
        this.targetObject = targetObject;
    }

    public static Object createProxy(String fieldName, Object target) throws NoSuchFieldException {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new LazyLoadingInterceptor(fieldName, target.getClass().getField(fieldName)));
        return enhancer.create();
    }

    @Override
    public Object loadObject() throws Exception {
        Field field2 = targetObject.getClass().getDeclaredField(fieldName);
        field2.setAccessible(true);

        String originalName = (String) field2.get(targetObject);
        if(originalName == null) {
            field2.setAccessible(true);
            field2.set(targetObject, "lazy null");

            return targetObject;
        }
        String modifiedName = originalName.toUpperCase();

        field2.setAccessible(true);
        field2.set(targetObject, modifiedName);

        return targetObject;
    }
}
