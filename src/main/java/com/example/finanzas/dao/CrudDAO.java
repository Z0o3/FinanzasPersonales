package com.example.finanzas.dao;

import java.util.List;

public interface CrudDAO<T> {
    List<T> getAll(String usuarioId);
    void insert(T t);
    void insertAll(List<T> lista);
    void update(T t);
    void delete(String usuarioId, String id);
}
