package com.meteor.app.repo;

import com.meteor.app.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepo extends CrudRepository<Order,Long> {
    @Query("select o from com.meteor.app.entity.Order o " +
            "join fetch o.orderItems where o.id=:id")
    Order findOrderAndItems(Long id);
}
