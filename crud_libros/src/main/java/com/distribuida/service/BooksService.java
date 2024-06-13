package com.distribuida.service;

import com.distribuida.db.Books;

import java.util.List;

public interface BooksService {

    Books findById(Integer id);

    List<Books> findAll();

    Integer insert(Books books);

    Boolean update(Books books);

    Boolean delete(Integer id);
}
