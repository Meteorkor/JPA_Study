package com.meteor.app.repo;

import com.meteor.app.entity.OrderItem;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepo extends CrudRepository<OrderItem,Long> {
}
