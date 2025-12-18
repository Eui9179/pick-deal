package com.leui.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.dto.OrderRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrdersControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 통합 테스트")
    @WithMockUser(username = "user1", roles = "USER")
    public void createOrder() throws Exception {
        // given
        OrderRequest request = OrderRequest.builder().build();
        MockHttpServletRequestBuilder builder = post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        // when, then
        mvc.perform(builder)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 ROLE 테스트")
    @WithMockUser(username = "user1", roles = "STORE")
    public void createOrder_ROLE_STORE() throws Exception {

        // given
        MockHttpServletRequestBuilder builder = post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("");
        // when

        //then
        mvc.perform(builder)
                .andExpect(status().isForbidden());

    }

}
