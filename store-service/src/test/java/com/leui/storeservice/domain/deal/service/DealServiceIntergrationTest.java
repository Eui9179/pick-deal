package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import com.leui.storeservice.domain.deal.dto.DealStockDecreaseRequest;
import com.leui.storeservice.domain.deal.dto.DealStockDecreaseResponse;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.entity.DealStatus;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.discountpolicy.calculator.DiscountCalculator;
import com.leui.storeservice.domain.exception.OutOfStock;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@PostgreSQLTestContainer
@Import({DealService.class, DiscountCalculator.class})
public class DealServiceIntergrationTest {

    @Autowired
    DealRepository dealRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    @Autowired
    DealService dealService;

    @BeforeEach
    void setup() {
        dealRepository.deleteAll();
        storeRepository.deleteAll();
        storeCategoryRepository.deleteAll();
    }

    @Test
    void testDecreaseStockQuantity_OnMultiThread() throws InterruptedException {
        //given
        int stockQuantity = 100;

        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test", "test"));
        Store store = storeRepository.save(new Store(
                "name",
                LocationUtils.createPoint(0.0, 0.0),
                "address",
                "phoneNumber",
                LocalTime.now(),
                category
        ));

        Deal deal = dealRepository.save(new Deal(
                store,
                "name",
                "description",
                BigDecimal.ZERO,
                stockQuantity,
                DealStatus.ON_SALE,
                LocalDateTime.now()
        ));


        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(stockQuantity);

        //when
        for (int i = 0; i < stockQuantity; i++) {
            executor.submit(() -> {
                try {
                    dealService.decreaseStock(deal.getId(), new DealStockDecreaseRequest(1));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        //then
        Deal result = dealService.getDeal(deal.getId());
        assertThat(result.getStockQuantity()).isEqualTo(0);
    }

    @Test
    void testDecreaseStockQuantity() {
        //given
        int stockQuantity = 1;
        int needStockQuntity = 1;

        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test", "test"));
        Store store = storeRepository.save(new Store(
                "name",
                LocationUtils.createPoint(0.0, 0.0),
                "address",
                "phoneNumber",
                LocalTime.now(),
                category
        ));

        Deal deal = dealRepository.save(new Deal(
                store,
                "name",
                "description",
                BigDecimal.ZERO,
                stockQuantity,
                DealStatus.ON_SALE,
                LocalDateTime.now()
        ));

        //when & then
        DealStockDecreaseResponse response = dealService.decreaseStock(deal.getId(), new DealStockDecreaseRequest(needStockQuntity));
        assertThat(response.stockQuantity()).isEqualTo(1);
    }

    @Test
    void testDecreaseStockQuantity_ThrowsOutOfStock() {
        //given
        int stockQuantity = 1;
        int needStockQuntity = 2;

        StoreCategory category = storeCategoryRepository.save(new StoreCategory("test", "test"));
        Store store = storeRepository.save(new Store(
                "name",
                LocationUtils.createPoint(0.0, 0.0),
                "address",
                "phoneNumber",
                LocalTime.now(),
                category
        ));

        Deal deal = dealRepository.save(new Deal(
                store,
                "name",
                "description",
                BigDecimal.ZERO,
                stockQuantity,
                DealStatus.ON_SALE,
                LocalDateTime.now()
        ));

        //when & then
        assertThrows(
                OutOfStock.class,
                () -> dealService.decreaseStock(deal.getId(), new DealStockDecreaseRequest(needStockQuntity))
        );
    }
}
