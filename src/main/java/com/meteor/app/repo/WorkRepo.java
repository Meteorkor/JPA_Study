package com.meteor.app.repo;

import com.meteor.app.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepo extends JpaRepository<Work,Long> {
}
