package com.leui.storeservice.domain.deal.controller;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import com.leui.storeservice.domain.deal.dto.DealsDetailResponse;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.entity.DealsStatus;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@PostgreSQLTestContainer
public class DealsControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DealsRepository dealsRepository;

    @Autowired
    StoresRepository storesRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Test
    @DisplayName("상품 단건 조회 테스트")
    void getDealDetail() {
        // given
        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test", "test"));

        Stores store = storesRepository.save(new Stores(
                "store_name",
                LocationUtils.createPoint(1.1, 1.1),
                "store_location",
                "store_address",
                LocalDateTime.now(),
                category));

        Deals deal = dealsRepository.save(new Deals(store,
                "deal_name",
                "deal_description",
                1,
                1,
                1,
                DealsStatus.ON_SALE,
                LocalDateTime.now()));

        String uri = "/api/v1/deals/" + deal.getId();

        // when
        ResponseEntity<DealsDetailResponse> response = restTemplate.getForEntity(uri, DealsDetailResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(deal.getId());
    }
}
