package by.zapolski.database.dao;

import by.zapolski.database.model.Entity;

import java.util.List;

public interface Dao<K, T extends Entity> {
    List<T> getAll();

    boolean create(T entity);

    T getById(K id);

    boolean update(T entity);

    boolean remove(K id);

    void close();
}
