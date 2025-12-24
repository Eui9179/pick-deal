package com.leui.storeservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.storeservice.domain.deal.dto.DealsDetailResponse;
import com.leui.storeservice.domain.deal.entity.DealCategory;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.entity.DealsStatus;
import com.leui.storeservice.domain.deal.repository.DealCategoryRepository;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.deal.service.DealsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class DealsControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    DealsService dealsService;

    @Autowired
    DealsRepository dealsRepository;

    @Autowired
    DealCategoryRepository dealCategoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("상품 단건 조회 테스트")
    void getDealDetail() throws Exception {

        // given
        Long id = 1L;
        String uri = "/api/v1/deals/" + id;

        // when
        MockHttpServletRequestBuilder builder = get(uri).contentType(MediaType.APPLICATION_JSON);

        // then
        MvcResult result = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        DealsDetailResponse responseBody = objectMapper
                .readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), DealsDetailResponse.class);

        Assertions.assertThat(responseBody.id()).isEqualTo(id);
    }
}
