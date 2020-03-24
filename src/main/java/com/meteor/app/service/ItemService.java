package com.meteor.app.service;

import com.meteor.app.entity.Item;
import com.meteor.app.entity.Member;
import com.meteor.app.repo.ItemRepo;
import com.meteor.app.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * 상품 기능
 * 	상품 등록
 * 	상품 수정
 * 	상품 조회
 */
public class ItemService {
    private final ItemRepo itemRepo;

    public List<Item> findItems(){
        List<Item> list = new ArrayList<>();
        itemRepo.findAll().forEach(list::add);
        return list;
    }
    public Optional<Item> findItem(Long id){
        return itemRepo.findById(id);
    }

    @Transactional
    public void regist(Item member){
        itemRepo.save(member);
    }

    @Transactional
    public void update(Long id, String name){
        itemRepo.findById(id).ifPresent(s->{
            s.setName(name);
        });
    }


}
