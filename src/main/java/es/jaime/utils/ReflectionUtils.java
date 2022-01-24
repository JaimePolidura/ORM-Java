package es.jaime.utils;

import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.nio.channels.Pipe;

import static java.lang.String.*;

public final class ReflectionUtils {
    private ReflectionUtils () {}

    @SneakyThrows
    public static Object invokeGetterMethod(Object instance, String field){
        String fieldNameWithFirstCharUpperCase = field.substring(0, 1).toUpperCase() + field.substring(1);
        String getterMethod = format("get%s", fieldNameWithFirstCharUpperCase);
        Method method = instance.getClass().getMethod(getterMethod);

        return method.invoke(instance);
    }

    @SneakyThrows
    public static Object invokeValueObjectMethodGetter(Object aggreageteInstance, String fieldAggregate, String fieldValueObject){
        Object valueObject = invokeGetterMethod(aggreageteInstance, fieldAggregate);

        return valueObject.getClass().getDeclaredMethod(fieldValueObject).invoke(valueObject);
    }

    @SneakyThrows
    public static Object invokeMethod(Object instance, String methodNmae){
        return instance.getClass().getDeclaredMethod(methodNmae).invoke(instance);
    }
}
