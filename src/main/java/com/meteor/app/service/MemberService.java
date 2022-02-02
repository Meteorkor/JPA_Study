package com.meteor.app.service;

import com.meteor.app.entity.Member;
import com.meteor.app.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 회원 기능
 *  회원 등록
 *  회원 조회
 */
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepo memberRepo;

    public Iterable<Member> findMembers() {
        return StreamSupport.stream(memberRepo.findAll().spliterator(), false).collect(Collectors.toList());
    }
    public Member findMember(Long id){
        return memberRepo.findById(id).orElseThrow();
    }
    @Transactional
    public void regist(Member member){
        memberRepo.save(member);
    }


}
