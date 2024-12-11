package com.iliasdev.dao;

import com.iliasdev.model.ExchangeRates;

import java.util.List;

public interface Dao<K, T>{
    T create(T t);
    T findById(K id);
    List<T> findAll();
    ExchangeRates update(T t);
    void delete(T t);
}
