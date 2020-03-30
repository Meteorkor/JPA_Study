package com.meteor.app.repo;

import com.meteor.app.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepo extends CrudRepository<Item,Long> {



}
