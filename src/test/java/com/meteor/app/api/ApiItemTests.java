package com.meteor.app.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meteor.app.dto.MemberDto;
import com.meteor.app.dto.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * API 호출 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ApiItemTests {
    @Autowired
    private MockMvc test;

    @Test
    public void member() throws Exception {
        String content = test.perform(MockMvcRequestBuilders.get("/api/v1/items"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Assertions.assertFalse("".equals(content) || content == null);
    }

    @Test
    public void memberRegistAndConfirm() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String registMemberName = "pa132";
        {
            String content = test.perform(MockMvcRequestBuilders.get("/api/v1/item/1"))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        String insertNo = "";
        {
            MemberDto dto = new MemberDto();
            dto.setName(registMemberName);


            String content = test.perform(MockMvcRequestBuilders.post("/api/v1/item")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto))
            )
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            Result<Long> ret = mapper.readValue(content, new TypeReference<Result<Long>>(){});
            insertNo = String.valueOf(ret.getData());
        }

        {
            String content = test.perform(MockMvcRequestBuilders.get("/api/v1/member/" + insertNo))
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            Result<MemberDto> ret = mapper.readValue(content, new TypeReference<Result<MemberDto>>(){});
            Assertions.assertEquals(registMemberName,ret.getData().getName());

        }
    }


}
