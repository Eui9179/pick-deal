package com.leui.storeservice.domain.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.dto.DealsDetailResponse;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.entity.DealsStatus;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.deal.service.DealsService;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@PostgreSQLTestContainer
public class DealsControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DealsService dealsService;

    @Autowired
    DealsRepository dealsRepository;

    @Autowired
    StoresRepository storesRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        setupData();
    }

    @Test
    @DisplayName("상품 단건 조회 테스트")
    void getDealDetail() {
        // given
        // TODO 이 블록에서 리소스 저장
        Long id = 1L;
        String uri = "/api/v1/deals/" + id;

        // when
        ResponseEntity<DealsDetailResponse> response = restTemplate.getForEntity(uri, DealsDetailResponse.class);

        // then
        Assertions.assertThat(response.getBody().id()).isEqualTo(id);
    }

    private void setupData() {
        storesRepository.deleteAll();
        storeCategoryRepository.deleteAll();

        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test code", "test desc"));
        Stores store = new Stores(
                "store_name",
                LocationUtils.createPoint(1.1, 1.1),
                "store_location",
                "store_address",
                LocalDateTime.now(),
                category);

        storesRepository.save(store);

        Deals deals = new Deals(
                store,
                "deal_name",
                "deal_description",
                1,
                1,
                1,
                DealsStatus.ON_SALE,
                LocalDateTime.now());

        dealsRepository.save(deals);
    }
}
