package com.meteor.app.ql;

//import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.meteor.app.dto.ItemDto;
import com.meteor.app.repo.ItemRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class ItemQuery implements GraphQLQueryResolver {
//    private final ItemRepo itemRepo;
//    public List<ItemDto> getAllItems() {
//        return StreamSupport.stream(itemRepo.findAll().spliterator(), false)
//                .map(item -> new ItemDto(item))
//                .collect(Collectors.toList());
//    }
//
//    public ItemDto getItem(long id) {
//        if(log.isInfoEnabled()){
//            log.info("getItem : {}", id);
//        }
//        return new ItemDto(itemRepo.findById(id).orElse(null));
//    }
//}