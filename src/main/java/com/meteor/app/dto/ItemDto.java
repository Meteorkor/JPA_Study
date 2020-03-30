package com.meteor.app.dto;

import com.meteor.app.entity.Item;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private int price;
    private Long stockQuantity;

    public ItemDto(Item s) {
        this.id = s.getId();
        this.name = s.getName();
        this.price = s.getPrice();
        this.stockQuantity = s.getStockQuantity();

    }

}
