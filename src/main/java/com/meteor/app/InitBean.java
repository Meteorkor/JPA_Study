package com.meteor.app;

import com.meteor.app.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InitBean {
    private final InsertClass insertClass;

    @PostConstruct
    public void init() {
        insertClass.insertEmpData();
        insertClass.insertStudyData();

    }


    @Component
    @RequiredArgsConstructor
    static class InsertClass{

        private final EntityManager entityManager;

        @Transactional
        public void insertStudyData(){

            Member mem1 = new Member();
            mem1.setName("kim");
            Member mem2 = new Member();
            mem2.setName("lee");
            entityManager.persist(mem1);
            entityManager.persist(mem2);
            {
                Order order1 = new Order();

                Item item = new Book();
                item.setName("book1");
                item.setPrice(1000);
                item.setStockQuantity(2L);
                entityManager.persist(item);
                OrderItem orderItem1 = OrderItem.builder().item(item).build();
                order1.addOrderItem(orderItem1);
                order1.setMember(mem1);
                entityManager.persist(orderItem1);
                entityManager.persist(order1);
            }


            {
                Order order2 = new Order();
                Item item = new Book();
                item.setName("book2");
                item.setPrice(1000);
                item.setStockQuantity(2L);
                entityManager.persist(item);
                OrderItem orderItem1 = OrderItem.builder().item(item).build();
                order2.addOrderItem(orderItem1);
                order2.setMember(mem1);
                entityManager.persist(orderItem1);
                entityManager.persist(order2);
            }





        }

        @Transactional
        public void insertEmpData(){
            EmpEntity emp1 = EmpEntity.builder().empno(0).ename("kim").build();
            EmpEntity emp2 = EmpEntity.builder().empno(1).ename("lee").build();
            entityManager.persist(emp1);
            entityManager.persist(emp2);
        }
    }


}
