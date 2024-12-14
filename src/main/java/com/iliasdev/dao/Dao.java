package com.iliasdev.dao;

import com.iliasdev.model.ExchangeRates;

import java.util.List;
import java.util.Optional;

public interface Dao<K, T>{
    T create(T t);
    Optional<T> findById(K id);
    List<T> findAll();
    ExchangeRates update(T t);
    void delete(T t);
}
