package com.leui.storeservice.domain.deal.service;

import dto.store.DealStockDecreaseRequest;
import dto.store.DealStockDecreaseResponse;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import exception.OutOfStock;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DealServiceUnitTest {

    @InjectMocks
    DealService dealService;

    @Mock
    DealRepository dealRepository;

    @Test
    void testDecreaseStock() {
        //given
        Long id = 1L;
        DealStockDecreaseRequest request = new DealStockDecreaseRequest(1);

        //mock
        doReturn(1).when(dealRepository).decreaseStockQuantity(any(), anyInt());

        //when
        DealStockDecreaseResponse response = dealService.decreaseStock(id, request);

        //then
        assertThat(response.stockQuantity()).isEqualTo(1);
    }

    @Test
    void testDecreaseStock_stockQunatityIsZero_ThrowOutOfStock() {
        //given
        Long id = 1L;
        DealStockDecreaseRequest request = new DealStockDecreaseRequest(1);

        //mock
        doReturn(0).when(dealRepository).decreaseStockQuantity(any(), anyInt());
        doReturn(true).when(dealRepository).existsById(any());

        //when
        assertThrows(OutOfStock.class, () -> dealService.decreaseStock(id, request));
    }

    @Test
    void testDecreaseStock_EntityNotExist_ThrowEntityNotFountException() {
        //given
        Long id = 1L;
        DealStockDecreaseRequest request = new DealStockDecreaseRequest(1);

        //mock
        doReturn(0).when(dealRepository).decreaseStockQuantity(any(), anyInt());
        doReturn(false).when(dealRepository).existsById(any());

        //when
        assertThrows(EntityNotFoundException.class, () -> dealService.decreaseStock(id, request));
    }
}
