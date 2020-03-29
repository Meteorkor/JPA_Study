package com.meteor.app.repo;

import com.meteor.app.entity.Order;
import lombok.RequiredArgsConstructor;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.query.criteria.internal.CriteriaQueryImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

@Repository
@RequiredArgsConstructor
public class OrderEnRepo {
    private final EntityManager entityManager;

    public Order findOrderAndItems(Long id){
        Order order = entityManager.createQuery("select o from com.meteor.app.entity.Order o " +
                "join fetch o.orderItems where o.id=:id", Order.class).setParameter("id", id)
                .getSingleResult();

        return order;
    }

}
