package com.meteor.app;

import com.meteor.app.entity.EmpEntity;
import org.springframework.data.repository.CrudRepository;

public interface EmpRepo extends CrudRepository<EmpEntity, Long> {
}
