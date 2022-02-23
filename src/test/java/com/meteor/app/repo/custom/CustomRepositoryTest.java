package com.meteor.app.repo.custom;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
