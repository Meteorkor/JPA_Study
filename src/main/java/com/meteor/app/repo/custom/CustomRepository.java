package com.meteor.app.repo.custom;

import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

import com.meteor.app.entity.Book;

@RepositoryDefinition(domainClass = Book.class, idClass = Long.class)
public interface CustomRepository {
    Book save(Book book);

    Optional<Book> findById(Long id);
}
