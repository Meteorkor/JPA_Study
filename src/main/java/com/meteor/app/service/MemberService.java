package com.meteor.app.service;

import com.meteor.app.entity.Member;
import com.meteor.app.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/**
 * 회원 기능
 *  회원 등록
 *  회원 조회
 */
public class MemberService {
    private final MemberRepo memberRepo;

    public Iterable<Member> findMembers(){
        List<Member> list = new ArrayList<>();
        memberRepo.findAll().forEach(list::add);
        return list;
    }
    public Member findMember(Long id){
        return memberRepo.findById(id).get();
    }
    @Transactional
    public void regist(Member member){
        memberRepo.save(member);
    }


}
