package com.meteor.app.controller;

import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meteor.app.dto.ItemDto;
import com.meteor.app.dto.Result;
import com.meteor.app.entity.Item;
import com.meteor.app.service.ItemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("items")
    public Result<Object> items() {
        return Result.builder().data(
                itemService.findItems().stream().map(ItemDto::fromEntity).collect(Collectors.toList())
        ).build();
    }

    @GetMapping("item/{id}")
    public Result<Object> items(@PathVariable("id") Long id) {
        return Result.builder().data(
                itemService.findItem(id).orElse(null)
        ).build();
    }

    @PostMapping("item")
    public Result<ItemDto> items(@RequestBody ItemDto itemDto) {
        Item item = itemDto.toEntity();
        itemService.regist(item);
        itemDto.setId(item.getId());
        return Result.<ItemDto>builder().data(itemDto).build();
    }

}
