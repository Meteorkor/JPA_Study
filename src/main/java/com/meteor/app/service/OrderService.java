package com.meteor.app.service;

import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import com.meteor.app.entity.Item;
import com.meteor.app.entity.Member;
import com.meteor.app.entity.Order;
import com.meteor.app.entity.OrderItem;
import com.meteor.app.repo.MemberRepo;
import com.meteor.app.repo.OrderEnRepo;
import com.meteor.app.repo.OrderItemRepo;
import com.meteor.app.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final MemberService memberService;
    private final OrderItemRepo orderItemRepo;
    private final EntityManager entityManager;

    private final OrderEnRepo orderEnRepo;

    @Transactional
    public Long order(Member member, OrderItem ... items){
        Member buyMember = memberService.findMember(member.getId());

        Order order = new Order();
        order.setMember(buyMember);
        for (OrderItem item : items) {
            orderItemRepo.save( item );
            order.addOrderItem(item);
        }
        order.setStatus(Order.OrderStatus.ORDER);
        orderRepo.save(order);
        return order.getId();
    }

    @Transactional
    public void orderCancel(Long id){
        Order order = findOrder(id);
        order.setStatus(Order.OrderStatus.CANCEL);
    }

    @Transactional
    public void orderModifyCount(Long id, long count){
        Order order = findOrder(id);
        Iterator<OrderItem> iter = order.getOrderItems().iterator();
        while(iter.hasNext()){
            OrderItem item = iter.next();
            item.setCount(count);
        }
    }

    public Order findOrder(Long id){
        return orderRepo.findById(id).get();
    }
//    @Transactional

    public Order findOrderAndItems(Long id){
        return orderEnRepo.findOrderAndItems(id);

    }

}
