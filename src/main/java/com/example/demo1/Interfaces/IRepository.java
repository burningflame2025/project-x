package com.example.demo1.Interfaces;

import java.util.List;

public interface IRepository<T> {
    void add(T item) throws Exception;
    void delete(int id) throws Exception;
    void update(T item) throws Exception;
    List<T> findAll() throws Exception;
    T findById(int id) throws Exception;
}