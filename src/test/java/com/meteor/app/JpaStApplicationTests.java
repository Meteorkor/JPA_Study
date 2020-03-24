package com.meteor.app;

import com.meteor.app.entity.*;
import com.meteor.app.repo.MemberRepo;
import com.meteor.app.service.ItemService;
import com.meteor.app.service.MemberService;
import com.meteor.app.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class JpaStApplicationTests {
	@Autowired
	private MemberRepo memberRepo;
	@Autowired
	private FindTestClass tester;
	@Autowired
	private MemberService memberService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private OrderService orderService;


	@Test
	public void empSelectTest() throws Exception {
		tester.printMemberAndOrder();
	}


	@Test
	@DisplayName("회원등록 및 조회 테스트")
	public void memberSvcRegisTest(){
		Member member = new Member();
		final String MEMBER_NAME = "신규회원";
		member.setName(MEMBER_NAME);
		memberService.regist(member);
		Member loadMember = memberService.findMember(member.getId());
		Assertions.assertEquals(MEMBER_NAME,loadMember.getName());
		Assertions.assertEquals(member,loadMember);
	}

	@Test
	@DisplayName("상품등록 및 조회 테스트")
	public void itemSvcRegisTest(){
		final String ITEM_NAME = "tempBook";
		Book book = new Book();
		book.setName(ITEM_NAME);
		book.setStockQuantity(10L);
		book.setPrice(1000);
		itemService.regist(book);
		Item loadBook = itemService.findItem(book.getId()).get();
		Assertions.assertEquals(ITEM_NAME,loadBook.getName());
		Assertions.assertEquals(book,loadBook);
	}

	@Test
	@DisplayName("상품주문 및 조회 테스트")
	public void orderSvcRegisTest(){
		final String ITEM_NAME = "tempBook";
		Member member = memberService.findMember(1L);

		List<OrderItem> orderItemList = itemService.findItems().stream().map(s->{
			OrderItem orderItem = new OrderItem();
			orderItem.setCount((long) 3);
			orderItem.setItem(s);
			orderItem.setOrderPrice((long) s.getPrice());
			return orderItem;
		}).collect(Collectors.toList());
		//모든 상품을 3개씩 구매

		OrderItem[] items = orderItemList.toArray(new OrderItem[orderItemList.size()]);
		Long orderId = orderService.order( member, items );

		//Order order = orderService.findOrder(orderId);

		Order order = orderService.findOrderAndItems(orderId);


		// TODO 여기도 영속성 끊겨서 안되네..;;;, 복사해서 넘겨야 하나..
		Assertions.assertEquals(order.getOrderItems().size(), orderItemList.size());

	}



	@Test
	public void uniqueErrorTest() throws Exception {



	}

}
