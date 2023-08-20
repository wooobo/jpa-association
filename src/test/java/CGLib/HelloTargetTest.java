package CGLib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
class HelloTargetTest {

    @Test
    void HelloUppercase_proxy() {
        HelloTarget helloTarget = new HelloTarget();
        HelloTarget proxiedHello = (HelloTarget) HelloUppercase.createProxy(helloTarget);

        assertThat(proxiedHello.sayHello("upper")).isEqualTo("HELLO UPPER");
        assertThat(proxiedHello.sayHi("upper")).isEqualTo("HI UPPER");
        assertThat(proxiedHello.sayThankYou("upper")).isEqualTo("THANK YOU UPPER");
    }

    @Test
    void LazyLoader() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HelloTarget helloTarget = new HelloTarget();
        String fieldValue = "name2";
//        HelloTarget proxiedHello = (HelloTarget) LazyLoadingInterceptor.createProxy(fieldValue, helloTarget);
//
//
//        String actual = proxiedHello.lazy2();
//
//        assertThat(actual).isEqualTo("lazy null");

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(HelloTarget.class);
        enhancer.setCallback((LazyLoader) () -> {
            System.out.println("lazy laoding check");
            Field field = helloTarget.getClass().getDeclaredField(fieldValue);
            field.setAccessible(true);

            String originalName = (String) field.get(helloTarget);
            if(originalName == null) {
                // Access private field using reflection
                field.setAccessible(true);
                field.set(helloTarget, "ok null value2"); // Update the field

                return helloTarget;
            }
            String modifiedName = originalName.toUpperCase(); // Modify the value

            // Access private field using reflection
            field.setAccessible(true);
            field.set(helloTarget, modifiedName); // Update the field

            return helloTarget;
        });

        HelloTarget proxy = (HelloTarget) enhancer.create();

        System.out.println(proxy.lazy2()); // This will print the modified uppercase name
//        System.out.println(proxy.lazy()); // This will print the modified uppercase name

    }
}
