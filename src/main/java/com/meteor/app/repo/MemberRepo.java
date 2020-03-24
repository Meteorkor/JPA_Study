package com.meteor.app.repo;

import com.meteor.app.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepo extends CrudRepository<Member,Long> {
}
