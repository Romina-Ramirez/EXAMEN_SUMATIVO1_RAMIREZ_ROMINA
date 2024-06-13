package com.distribuida.service;

import com.distribuida.db.Books;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
@Named("booksService")
public class BooksServiceImpl implements BooksService {

    @Inject
    private EntityManager em;

    @Override
    public Books findById(Integer id) {
        return em.find(Books.class, id);
    }

    @Override
    public List<Books> findAll() {
        return em.createQuery("SELECT b FROM Books b", Books.class).getResultList();
    }

    @Override
    public Integer insert(Books books) {
        try {
            em.getTransaction().begin();
            em.persist(books);
            em.getTransaction().commit();
            return books.getId();
        } catch (Exception ex) {
            em.getTransaction().rollback();
            return 0;
        }
    }

    @Override
    public Boolean update(Books books) {
        try {
            em.getTransaction().begin();
            em.merge(books);
            em.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public Boolean delete(Integer id) {
        Books books = findById(id);

        if(books != null) {
            try {
                em.getTransaction().begin();
                em.remove(books);
                em.getTransaction().commit();
                return true;
            } catch (Exception ex) {
                em.getTransaction().rollback();
                return false;
            }
        } else {
            return false;
        }
    }
}
