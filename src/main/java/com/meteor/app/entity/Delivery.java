package com.meteor.app.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name="delivery_id")
    private Long id;
    @OneToOne(mappedBy = "delivery",fetch = FetchType.LAZY)
    private Order order;
    @Embedded
    private Member.Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    static enum DeliveryStatus{
        /**
         * 준비
         */
        READY,
        /**
         * 배송
         */
        COMP
    }
}
