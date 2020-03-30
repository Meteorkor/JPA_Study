package com.meteor.app.service;

import com.meteor.app.entity.Member;
import com.meteor.app.entity.Order;
import com.meteor.app.entity.OrderItem;
import com.meteor.app.repo.OrderEnRepo;
import com.meteor.app.repo.OrderItemRepo;
import com.meteor.app.repo.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    private final MemberService memberService;
    private final OrderItemRepo orderItemRepo;

    @Deprecated
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
        List<OrderItem> list =  order.getOrderItems();
        list.stream().forEach(s->s.setCount(count));
    }

    public Order findOrder(Long id){
        return orderRepo.findById(id).get();

    }
//    @Transactional

    public Order findOrderAndItems(Long id){
//        return orderEnRepo.findOrderAndItems(id);
        return orderRepo.findOrderAndItems(id);

    }

}
