package com.justbelieveinmyself.portscanner.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
@SuppressWarnings("unchecked")
public class Injector {
    private final Map<Class<?>, Object> instances = new LinkedHashMap<>();
    {
        register(Injector.class, this);
    }

    public <T> void register(Class<T> type, T impl) {
        instances.put(type, impl);
    }

    public void register(Class<?>... types) {
        stream(types).forEach(this::require);
    }

    public <T> T require(Class<T> type) {
        T value = (T) instances.get(type);
        if (value == null) {
            instances.put(type, value = createInstance(type));
        }
        return value;
    }

    public <T> List<T> requireAll(Class<T> type) {
        return instances.entrySet().stream().filter(e -> type.isAssignableFrom(e.getKey())).map(e -> (T) e.getValue()).collect(Collectors.toList());
    }

    private Class<?> collectionItemType(Type type) {
        return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private Class<?> toClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        else throw new InjectorException(type + " не поддерживаемый");
    }

    private boolean isCollection(Type type) {
        return type instanceof ParameterizedType && Collection.class.isAssignableFrom(toClass(type));
    }

    private <T> T createInstance(Class<T> type) {
        Constructor<T> constructor = (Constructor<T>) stream(type.getConstructors())
                .max(Comparator.comparing(Constructor::getParameterCount))
                .orElseThrow(() -> new InjectorException("Нет публичного конструктора " + type));
        try {
            return constructor.newInstance(resolveDeps(constructor));
        } catch (Throwable e) {
            throw new InjectorException("Не удалось создать " + type.getName() + ", зависимости: " + Arrays.toString(constructor.getParameterTypes()), e);
        }
    }

    private <T> Object[] resolveDeps(Constructor<T> constructor) {
        return stream(constructor.getGenericParameterTypes())
                .map(t -> isCollection(t) ? requireAll(collectionItemType(t)) : require(toClass(t))).toArray();
    }

}
