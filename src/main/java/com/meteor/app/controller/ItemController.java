package com.meteor.app.controller;

import com.meteor.app.dto.ItemDto;
import com.meteor.app.dto.Result;
import com.meteor.app.repo.ItemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepo itemRepo;

    @GetMapping("items")
    public Result<Object> items(){
        return
        Result.builder().data(
            StreamSupport.stream(itemRepo.findAll().spliterator(), false)
                    .map(s->new ItemDto(s)).collect(Collectors.toList())
        ).build();
    }

    @GetMapping("item/{id}")
    public Result<Object> items(@PathVariable("id") Long id){
        return
                Result.builder().data(
                        itemRepo.findById(id).get()
                ).build();
    }

}
