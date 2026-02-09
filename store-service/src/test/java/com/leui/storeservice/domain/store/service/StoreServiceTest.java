package com.leui.storeservice.domain.store.service;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoreRepository;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@PostgreSQLTestContainer
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StoreService.class)
class StoreServiceTest {

    @Autowired
    StoreService storeService;

    @Autowired
    StoreRepository repository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        storeCategoryRepository.deleteAll();

        StoreCategory category = storeCategoryRepository.save(new StoreCategory("orderId code", "orderId desc"));
        // 1. radius = 100m
        repository.save(new Store("name1_100", LocationUtils.createPoint(126.9725445, 37.5557536),
                "address1_100", "phoneNumber1", LocalTime.now(), category));
        repository.save(new Store("name2_100", LocationUtils.createPoint(126.9736745, 37.5548556),
                "address2_100", "phoneNumber2", LocalTime.now(), category));
        repository.save(new Store("name3_100", LocationUtils.createPoint(126.9717455, 37.5542206),
                "address3_100", "phoneNumber3", LocalTime.now(), category));

        // 2. radius = 200m
        repository.save(new Store("name1_200", LocationUtils.createPoint(126.9725445, 37.5566520),
                "address1_200", "phoneNumber4", LocalTime.now(), category));
        repository.save(new Store("name2_200", LocationUtils.createPoint(126.9748045, 37.5548556),
                "address2_200", "phoneNumber5", LocalTime.now(), category));
        repository.save(new Store("name3_200", LocationUtils.createPoint(126.9711460, 37.5535900),
                "address3_200", "phoneNumber6", LocalTime.now(), category));

        // 3. radius = 300
        repository.save(new Store("name1_300", LocationUtils.createPoint(126.9725445, 37.5575500),
                "address1_300", "phoneNumber7", LocalTime.now(), category));
    }

    @Test
    void testFindNear_radius100() {
        //given
        double x = 126.9725445;
        double y = 37.5548556;
        int radius = 100;

        //when
        List<StoreInfoResponse> stores = storeService.getNearStores(new StoreFindRequest(x, y, radius));

        //then
        assertThat(stores)
                .extracting(StoreInfoResponse::name)
                .containsExactlyInAnyOrder(
                        "name1_100",
                        "name2_100",
                        "name3_100"
                );
        assertThat(stores)
                .extracting(StoreInfoResponse::name)
                .doesNotContain("name1_300");
    }

    @Test
    void testFindNear_radius200() {
        //given
        double x = 126.9725445;
        double y = 37.5548556;
        int radius = 200;

        //when
        List<StoreInfoResponse> stores = storeService.getNearStores(new StoreFindRequest(x, y, radius));

        //then
        assertThat(stores)
                .extracting(StoreInfoResponse::name)
                .containsExactlyInAnyOrder(
                        "name1_100",
                        "name2_100",
                        "name3_100",
                        "name1_200",
                        "name2_200",
                        "name3_200"
                );

        assertThat(stores)
                .extracting(StoreInfoResponse::name)
                .doesNotContain("name1_300");
    }

}
