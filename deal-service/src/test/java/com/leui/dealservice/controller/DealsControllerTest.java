package com.leui.dealservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.dealservice.dto.DealsDetailResponse;
import com.leui.dealservice.entity.Category;
import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.entity.DealsStatus;
import com.leui.dealservice.repository.CategoryRepository;
import com.leui.dealservice.repository.DealsRepository;
import com.leui.dealservice.service.DealsService;
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
    CategoryRepository categoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        setupDeals();
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

        Assertions.assertThat(responseBody.getId()).isEqualTo(id);
    }

    private void setupDeals() {
        List<Deals> deals = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Category category = categoryRepository.save(Category.builder().name("CATEGORY" + i).build());
            deals.add(Deals.builder()
                    .storeId((long) i)
                    .category(category)
                    .dealsStatus(DealsStatus.ON_SALE)
                    .name("test name")
                    .description("test description")
                    .price(1000)
                    .discountPrice(700)
                    .stockQuantity(10)
                    .pickupEndTime(LocalDateTime.now())
                    .build());
        }
        dealsRepository.saveAll(deals);
    }
}
