package com.leui.storeservice.domain.deal.service;

import dto.store.DealStockQuantityRequest;
import dto.store.DealQuantityResponse;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import exception.OutOfStockException;
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
        DealStockQuantityRequest request = new DealStockQuantityRequest("1", 1);

        //mock
        doReturn(1).when(dealRepository).decreaseStockQuantity(any(), anyInt());

        //when
        DealQuantityResponse response = dealService.confirmStock(id, request.orderId(), request.quantity());

        //then
        assertThat(response.quantity()).isEqualTo(1);
    }

    @Test
    void testDecreaseStock_stockQunatityIsZero_ThrowOutOfStock() {
        //given
        Long id = 1L;
        DealStockQuantityRequest request = new DealStockQuantityRequest("1", 1);

        //mock
        doReturn(0).when(dealRepository).decreaseStockQuantity(any(), anyInt());
        doReturn(true).when(dealRepository).existsById(any());

        //when
        assertThrows(OutOfStockException.class, () -> dealService.confirmStock(id, request.orderId(), request.quantity()));
    }

    @Test
    void testDecreaseStock_EntityNotExist_ThrowEntityNotFountException() {
        //given
        Long id = 1L;
        DealStockQuantityRequest request = new DealStockQuantityRequest("1", 1);

        //mock
        doReturn(0).when(dealRepository).decreaseStockQuantity(any(), anyInt());
        doReturn(false).when(dealRepository).existsById(any());

        //when
        assertThrows(EntityNotFoundException.class, () -> dealService.confirmStock(id, "1", 1));
    }
}
