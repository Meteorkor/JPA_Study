package com.meteor.app;

import com.meteor.app.repo.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class FindTestClass {
    @Autowired
    private MemberRepo memberRepo;

    @Transactional
    public void printMember(){
        System.out.println("==============");
        memberRepo.findAll().forEach(s->{
            System.out.println("member : " + s.toString());

        });
        System.out.println("==============");
    }

    @Transactional
    public void printMemberAndOrder(){
        System.out.println("==============");
        memberRepo.findAll().forEach(s->{
            System.out.println("member : " + s.toString());
            s.getOrders().forEach(order->{
                System.out.println("order : " + order.getId());
            });
        });
        System.out.println("==============");
    }
}
