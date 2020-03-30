package com.meteor.app.controller;

import com.meteor.app.dto.MemberDto;
import com.meteor.app.dto.Result;
import com.meteor.app.entity.Member;
import com.meteor.app.repo.MemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class MemberController {
    private final MemberRepo memberRepo;

    @GetMapping("members")
    public Result<Object> getMembers(){
        List<MemberDto> memberDtoList = StreamSupport
                .stream(memberRepo.findAll()
                        .spliterator(), false)
                .map(MemberDto::new).collect(Collectors.toList());
        return
                Result.builder().data(memberDtoList)
                        .build();
    }

    @GetMapping("member/{id}")
    public Result<Object> getMembers(@PathVariable("id") Long id){

        return Result.builder().data( new MemberDto(memberRepo.findById(id).get()) ).build();
    }

    @PostMapping("member")
    public Result<Object> memberRegist(@RequestBody MemberDto memberDto){
        System.out.println("memberDto : " + memberDto.toString());
        return Result.builder().data( memberRepo.save( memberDto.toMember() ).getId() ).build();
    }
}
