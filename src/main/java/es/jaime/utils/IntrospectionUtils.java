package es.jaime.utils;

import java.lang.reflect.Field;
import java.util.*;

public final class IntrospectionUtils {
    private IntrospectionUtils() {}

    public static <T> List<String> getFieldsNames(Class<? extends T>[] classesToGetFields){
        Set<String> fields = new HashSet<>();

        for (Class<? extends T> classesToGetField : classesToGetFields) {
            addFields(fields, classesToGetField);
        }

        return new ArrayList<>(fields);
    }

    private static <T> void addFields(Set<String> fields, Class<? extends T> classesToGetField) {
        Class<?> classToCheck = classesToGetField;

        while (classToCheck != null && classToCheck != Object.class){
            for(Field field : classToCheck.getDeclaredFields()){
                fields.add(field.getName());
            }

            classToCheck = classToCheck.getSuperclass();
        }
    }
}
