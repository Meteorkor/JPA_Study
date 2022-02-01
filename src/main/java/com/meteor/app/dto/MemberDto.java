package com.meteor.app.dto;

import com.meteor.app.entity.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class MemberDto {
    private Long id;
    private String name;
    private Member.Address address;

    public static MemberDto fromEntity(Member member) {
        if (member == null) {return null;}
        return builder().id(member.getId()).name(member.getName()).address(member.getAddress())
                        .build();
    }

    public Member toEntity() {
        Member member = new Member();
        member.setId(this.id);
        member.setName(this.name);
        member.setAddress(this.address);
        return member;
    }
}
