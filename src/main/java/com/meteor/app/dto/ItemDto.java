package com.meteor.app.dto;

import com.meteor.app.entity.Book;
import com.meteor.app.entity.Item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class ItemDto {
    private Long id;
    private String name;
    private int price;
    private Long stockQuantity;

    public static ItemDto fromEntity(Item s) {
        return ItemDto.builder().id(s.getId()).name(s.getName()).price(s.getPrice()).stockQuantity(
                s.getStockQuantity()).build();
    }

    public Item toEntity() {
        Book book = new Book();
        book.setId(id);
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        return book;
    }
}
