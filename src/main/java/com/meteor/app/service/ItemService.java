package com.meteor.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.meteor.app.dto.ItemDto;
import com.meteor.app.entity.Item;
import com.meteor.app.repo.ItemRepo;

import lombok.RequiredArgsConstructor;

/**
 * 상품 기능
 * 	상품 등록
 * 	상품 수정
 * 	상품 조회
 */
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepo itemRepo;

    public List<ItemDto> findItems() {
        return StreamSupport.stream(itemRepo.findAll().spliterator(), false).map(ItemDto::fromEntity).collect(
                Collectors.toList());
    }

    public Optional<Item> findItem(Long id) {
        return itemRepo.findById(id);
    }

    @Transactional
    public void regist(Item member) {
        itemRepo.save(member);
    }

    @Transactional
    public void update(Long id, String name) {
        itemRepo.findById(id).ifPresent(s -> {
            s.setName(name);
        });
    }

    @Transactional
    public void quantityUp(long id, long quantity) {
        Optional<Item> byIdForUpdate = itemRepo.findByIdForUpdate(id);
        byIdForUpdate.ifPresent(item -> {
            item.setStockQuantity(item.getStockQuantity() + quantity);
        });
    }
}
