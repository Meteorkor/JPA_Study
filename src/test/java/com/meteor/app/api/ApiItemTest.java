package com.meteor.app.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteor.app.dto.ItemDto;
import com.meteor.app.dto.MemberDto;
import com.meteor.app.dto.Result;

/**
 * API 호출 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ApiItemTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void itemsTest() throws Exception {
        String content = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/items"))
                                .andExpect(
                                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                .andReturn()
                                .getResponse()
                                .getContentAsString();
        Assertions.assertFalse("".equals(content) || content == null);
    }

    @Test
    public void itemRegistAndFindOne() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String registMemberName = "pa132";
        Long insertNo;
        {
            MemberDto dto = new MemberDto();
            dto.setName(registMemberName);

            String content = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/item")
                                                                   .contentType(MediaType.APPLICATION_JSON)
                                                                   .content(mapper.writeValueAsString(dto))
                                    )
                                    .andExpect(MockMvcResultMatchers.content()
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                    .andReturn()
                                    .getResponse()
                                    .getContentAsString();

            Result<ItemDto> ret = mapper.readValue(content, new TypeReference<Result<ItemDto>>() {});
            ItemDto data = ret.getData();
            insertNo = data.getId();
        }
        {
            String content = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/item/" + insertNo))
                                    .andExpect(MockMvcResultMatchers.content()
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                    .andReturn()
                                    .getResponse()
                                    .getContentAsString();
            Assertions.assertEquals(
                    "{\"data\":{\"id\":1,\"name\":\"pa132\",\"price\":0,\"stockQuantity\":null,\"categories\":[],\"author\":null,\"isbn\":null}}",
                    content);
//            System.out.println("content : " + content);
        }

//        {
//            String content = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/member/" + insertNo))
//                                    .andExpect(MockMvcResultMatchers.content()
//                                                                 .contentType(MediaType.APPLICATION_JSON))
//                                    .andReturn()
//                                    .getResponse()
//                                    .getContentAsString();
//            Result<MemberDto> ret = mapper.readValue(content, new TypeReference<Result<MemberDto>>() {});
//            Assertions.assertEquals(registMemberName, ret.getData().getName());
//
//        }
    }

}
