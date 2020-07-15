package com.meteor.app;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
class AppApplicationTests {
	@Autowired
	private MockMvc test;

	@Test
	public void contextLoads() throws Exception {
		String content = test.perform(MockMvcRequestBuilders.get("/index"))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn()
				.getResponse()

				.getContentAsString();
		System.out.println("content : " + content);


	}

	@Test
	public void empsTest() throws Exception {
		String content = test.perform(MockMvcRequestBuilders.get("/api/v1/emps"))
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andReturn()
				.getResponse()

				.getContentAsString();
		System.out.println("content : " + content);


	}

}
