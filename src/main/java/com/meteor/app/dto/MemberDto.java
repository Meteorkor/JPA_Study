package com.meteor.app.dto;

import com.meteor.app.entity.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private Member.Address address;

    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.address = member.getAddress();
    }

    public Member toMember(){
        Member member = new Member();
        member.setId(this.id);
        member.setName(this.name);
        member.setAddress(this.address);
        return member;
    }

}
