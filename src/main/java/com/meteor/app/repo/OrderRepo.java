package com.meteor.app.repo;

import com.meteor.app.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepo extends CrudRepository<Order,Long> {
}
