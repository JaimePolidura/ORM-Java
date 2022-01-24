package es.jaime.repository;

import java.util.List;
import java.util.Optional;

public abstract class Repostitory<T> {
    protected abstract List<T> all();
    protected abstract Optional<T> findById(Object id);
    protected abstract void save(T toPersist);
    protected abstract void deleteById(Object id);
}
