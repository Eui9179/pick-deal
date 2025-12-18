package com.leui.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.dto.OrderCreateRequest;
import com.leui.orderservice.dto.OrderDetailResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrdersControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("/orders 주문 통합 테스트")
    @WithMockUser(username = "user1", roles = "USER")
    public void createOrder() throws Exception {
        // given
        String uri = "/orders";
        OrderCreateRequest request = OrderCreateRequest.builder()
                .storeId(1L)
                .productId(1L)
                .quantity(1)
                .pickupTime(LocalDateTime.now().plusMinutes(30))
                .build();


        // when
        MockHttpServletRequestBuilder builder = post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        // then
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L));
    }

    @Test
    @DisplayName("/orders 주문 ROLE_STORE 테스트")
    @WithMockUser(username = "user1", roles = "STORE")
    public void createOrder_ROLE_STORE() throws Exception {
        // given
        String uri = "/orders";


        // when
        MockHttpServletRequestBuilder builder = post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .content("");

        // then
        mvc.perform(builder)
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("/orders/id 주문 조회 테스트")
    @WithMockUser(username = "user1", roles = {"USER", "STORE"})
    public void getOrderDetails() throws Exception {
        // given
        long orderId = 1L;
        String uri = "/orders";

        // when
        MockHttpServletRequestBuilder builder = get(uri + "/" + orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("");

        // then
        mvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(orderId));
    }

}
