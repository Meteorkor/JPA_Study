package com.meteor.app;

import com.meteor.app.entity.*;
import com.meteor.app.repo.MemberRepo;
import com.meteor.app.repo.WorkRepo;
import com.meteor.app.service.ItemService;
import com.meteor.app.service.MemberService;
import com.meteor.app.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@SpringBootTest
class JpaStApplicationTests {
    @Autowired
    private MemberRepo memberRepo;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private WorkRepo workRepo;

//    @Test
//    @Transactional
    public void bulkSaveTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (int i = 0; i < 10000; i++) {
            Work work = new Work();
            work.setId((long) i);
            work.setWorkStr("Book" + i);
            workRepo.save(work);

        }

        stopWatch.stop();
        System.out.println("stop : " + stopWatch.getTotalTimeMillis());

    }

//    @Test
    public void bulkSaveAllTest() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Work> list =
                IntStream.range(0, 100000).mapToObj(i -> {
                    Work work = new Work();
                    work.setId((long) i);
                    work.setWorkStr("Book" + i);
                    return work;
                }).collect(Collectors.toList());
        //save와 saveAll의 동작 차이는
        //saveAll 에서 트랜잭션을 시작하기 때문에,
        //save에서는 트랜잭션이 Propagation.REQUIRED 이기 때문에 별다른 작업을 하지 않음
        //for loop으로 save를 호출하는 경우 매번 Transactional이 수행
        workRepo.saveAll(list);
        stopWatch.stop();
        System.out.println("stop : " + stopWatch.getTotalTimeMillis());

    }


//    @Test
    @DisplayName("회원등록 및 조회 테스트")
    public void memberSvcRegisTest() {
        Member member = new Member();
        final String MEMBER_NAME = "신규회원";
        member.setName(MEMBER_NAME);
        memberService.regist(member);
        Member loadMember = memberService.findMember(member.getId());
        Assertions.assertEquals(MEMBER_NAME, loadMember.getName());
        Assertions.assertEquals(member, loadMember);
    }

//    @Test
    @DisplayName("상품등록 및 조회 테스트")
    public void itemSvcRegisTest() {
        final String ITEM_NAME = "tempBook";
        Book book = new Book();
        book.setName(ITEM_NAME);
        book.setStockQuantity(10L);
        book.setPrice(1000);
        itemService.regist(book);
        Item loadBook = itemService.findItem(book.getId()).get();
        Assertions.assertEquals(ITEM_NAME, loadBook.getName());
        Assertions.assertEquals(book, loadBook);
    }

//    @Test
    @DisplayName("상품주문 및 조회 테스트")
    public void orderSvcRegisTest() {
        final String ITEM_NAME = "tempBook";
        Member member = memberService.findMember(1L);

        List<OrderItem> orderItemList = itemService.findItems().stream().map(s -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setCount((long) 3);
            orderItem.setItem(s);
            orderItem.setOrderPrice((long) s.getPrice());
            return orderItem;
        }).collect(Collectors.toList());
        //모든 상품을 3개씩 구매

        OrderItem[] items = orderItemList.toArray(new OrderItem[orderItemList.size()]);
        Long orderId = orderService.order(member, items);
        Order order = orderService.findOrderAndItems(orderId);

        Assertions.assertEquals(order.getOrderItems().size(), orderItemList.size());
    }


//    @Test
    public void uniqueErrorTest() throws Exception {


    }

}
