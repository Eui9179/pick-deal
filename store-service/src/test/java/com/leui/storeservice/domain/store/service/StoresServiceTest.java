package com.leui.storeservice.domain.store.service;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import com.leui.storeservice.testcommon.postgres.PostgreSQLTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@PostgreSQLTestContainer
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(StoresService.class)
class StoresServiceTest {

    @Autowired
    StoresService storesService;

    @Autowired
    StoresRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // 1. radius = 100m
        repository.save(Stores.create("name1_100", LocationUtils.createPoint(126.9725445, 37.5557536), "address1_100", "phoneNumber1", LocalDateTime.now()));
        repository.save(Stores.create("name2_100", LocationUtils.createPoint(126.9736745, 37.5548556), "address2_100", "phoneNumber2", LocalDateTime.now()));
        repository.save(Stores.create("name3_100", LocationUtils.createPoint(126.9717455, 37.5542206), "address3_100", "phoneNumber3", LocalDateTime.now()));

        // 2. radius = 200m
        repository.save(Stores.create("name1_200", LocationUtils.createPoint(126.9725445, 37.5566520), "address1_200", "phoneNumber4", LocalDateTime.now()));
        repository.save(Stores.create("name2_200", LocationUtils.createPoint(126.9748045, 37.5548556), "address2_200", "phoneNumber5", LocalDateTime.now()));
        repository.save(Stores.create("name3_200", LocationUtils.createPoint(126.9711460, 37.5535900), "address3_200", "phoneNumber6", LocalDateTime.now()));

        // 3. radius = 300
        repository.save(Stores.create("name1_300", LocationUtils.createPoint(126.9725445, 37.5575500), "address1_300", "phoneNumber7", LocalDateTime.now()));
    }

    @Test
    void testFindNear_radius100() {
        //given
        double x = 126.9725445;
        double y = 37.5548556;
        int radius = 100;

        //when
        List<StoreInfoResponse> stores = storesService.getNearStores(new StoreFindRequest(x, y, radius));

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
        List<StoreInfoResponse> stores = storesService.getNearStores(new StoreFindRequest(x, y, radius));

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
