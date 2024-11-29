package com.iliasdev.dao;

import java.util.List;

public interface Dao<K, T>{
    T create(T t);
    T findById(K id);
    List<T> findAll();
    void update(T t);
    void delete(T t);
}
