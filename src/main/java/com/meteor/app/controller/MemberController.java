package com.meteor.app.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.meteor.app.dto.MemberDto;
import com.meteor.app.dto.Result;
import com.meteor.app.entity.Member;
import com.meteor.app.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("members")
    public Result<Object> getMembers() {

        List<MemberDto> memberDtoList = StreamSupport
                .stream(memberService.findMembers().spliterator(), false)
                .map(MemberDto::fromEntity).collect(Collectors.toList());
        return Result.builder().data(memberDtoList).build();
    }

    @GetMapping("member/{id}")
    public Result<Object> getMembers(@PathVariable("id") Long id) {
        return Result.builder().data(MemberDto.fromEntity(memberService.findMember(id))).build();
    }

    @PostMapping("member")
    public Result<Object> memberRegist(@RequestBody MemberDto memberDto) {
        Member member = memberDto.toEntity();
        memberService.regist(member);
        memberDto.setId(member.getId());
        return Result.builder().data(memberDto).build();
    }
}
