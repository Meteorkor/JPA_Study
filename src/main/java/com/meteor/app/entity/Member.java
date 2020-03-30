package com.meteor.app.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Member {
    @Id
    @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Order> orders = new ArrayList<>();


    @Embeddable
    @Getter
    public static class Address {
        protected Address(){

        }

        public Address(String city, String street, String zipCode) {
            this.city = city;
            this.street = street;
            this.zipCode = zipCode;
        }

        private String city;
        private String street;
        private String zipCode;
    }
}
