package es.jaime.utils;

import java.lang.reflect.Field;
import java.util.*;

public final class IntrospectionUtils {
    private IntrospectionUtils() {}

    public static List<String> getFieldsNames(Class<?> classToGetFields){
        List<String> toReturn = new ArrayList<>();
        Class<?> classToCheck = classToGetFields;

        while (classToCheck != null && classToCheck != Object.class){
            for(Field field : classToCheck.getDeclaredFields()){
                toReturn.add(field.getName());
            }

            classToCheck = classToGetFields.getSuperclass();
        }

        return toReturn;
    }
}
