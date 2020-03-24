package com.meteor.app;

import com.meteor.app.entity.EmpEntity;
import com.meteor.app.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//@SpringBootTest
@RequiredArgsConstructor
//@WebMvcTest
@SpringBootTest
class EmpJpaApplicationTests {
	@Autowired
	private EmpRepo empRepo;

	@Test
	public void empSelectTest() throws Exception {
		empRepo.findAll().forEach(s->{
			System.out.println("s : " + s.toString());
		});
	}



	@Test
	@DisplayName("유니크 에러 테스트")
	public void uniqueErrorTest() throws Exception {
		try{
			EmpEntity emp = new EmpEntity();
			emp.setEmpno(0);
			emp.setEname("kim");
			empRepo.save(emp);
			Assertions.fail("expected Exception");
		}catch (Throwable ignore ){

		}



	}

}
