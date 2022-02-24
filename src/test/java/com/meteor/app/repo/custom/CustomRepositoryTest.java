package com.meteor.app.repo.custom;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.meteor.app.entity.Book;

@SpringBootTest
public class CustomRepositoryTest {
    @Autowired
    private CustomRepository customRepository;

    @Test
    void findById() {
        Long id = 1L;
        int price = 100;
        String name = "name";

        Book book = new Book();
        book.setPrice(price);
        book.setName(name);
        customRepository.save(book);

        Optional<Book> byId = customRepository.findById(id);

        Assertions.assertThat(byId).get().matches(bok -> name.equals(bok.getName()) && price == bok.getPrice());
    }

    @Test
    void findByIdCustom() {
        Long id = 1L;
        int price = 100;
        String name = "name";

        Book book = new Book();
        book.setPrice(price);
        book.setName(name);
        customRepository.save(book);

        Optional<Book> byId = customRepository.findByIdCustom(id);

        Assertions.assertThat(byId).get().matches(bok -> name.equals(bok.getName()) && price == bok.getPrice());
    }

    @Test
    void findAllPaging() {
        int price = 100;
        String name = "name";

        int size = 100;
        for (int i = 0; i < size; i++) {
            Book book = new Book();
            book.setPrice(price);
            book.setName(name);
            customRepository.save(book);
        }

        Pageable unpaged = Pageable.unpaged();
        Page<Book> list = customRepository.findAllPaging(unpaged);
        Assertions.assertThat(list).hasSize(size);
    }

    @Test
    void findAllPagingFirst() {
        int price = 100;
        String name = "name";

        int size = 100;
        for (int i = 0; i < size; i++) {
            Book book = new Book();
            book.setPrice(price);
            book.setName(name);
            customRepository.save(book);
        }

        int perSize = 10;
        Pageable first = Pageable.ofSize(perSize);
        Page<Book> list = customRepository.findAllPaging(first);
        Assertions.assertThat(list).hasSize(perSize);
    }
}
