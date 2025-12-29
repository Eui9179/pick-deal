package com.leui.storeservice.domain.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.dto.DealsDetailResponse;
import com.leui.storeservice.domain.deal.entity.DealCategory;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.entity.DealsStatus;
import com.leui.storeservice.domain.deal.repository.DealCategoryRepository;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.deal.service.DealsService;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@ActiveProfiles("postgres")
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
    StoresRepository storesRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        setupData();
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

    private void setupData() {
        storesRepository.deleteAll();

        Stores store = Stores.create(
                "store_name",
                LocationUtils.createPoint(1.1, 1.1),
                "store_location",
                "store_address",
                LocalDateTime.now());

        storesRepository.save(store);

        DealCategory category = dealCategoryRepository.save(DealCategory.create("category"));

        Deals deals = Deals.create(
                store,
                "deal_name",
                "deal_description",
                1,
                1,
                1,
                DealsStatus.ON_SALE,
                LocalDateTime.now(),
                category);

        dealsRepository.save(deals);
    }
}
