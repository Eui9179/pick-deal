package com.leui.storeservice.domain.deal.controller;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import com.leui.storeservice.domain.deal.dto.DealDetailResponse;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealStatus;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoreRepository;
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
public class DealControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    DealRepository dealRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Test
    @DisplayName("상품 단건 조회 테스트")
    void getDealDetail() {
        // given
        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test", "test"));

        Store store = storeRepository.save(new Store(
                "store_name",
                LocationUtils.createPoint(1.1, 1.1),
                "store_location",
                "store_address",
                LocalDateTime.now(),
                category));

        Deal deal = dealRepository.save(new Deal(store,
                "deal_name",
                "deal_description",
                1,
                1,
                1,
                DealStatus.ON_SALE,
                LocalDateTime.now()));

        String uri = "/api/v1/deals/" + deal.getId();

        // when
        ResponseEntity<DealDetailResponse> response = restTemplate.getForEntity(uri, DealDetailResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(deal.getId());
    }
}
