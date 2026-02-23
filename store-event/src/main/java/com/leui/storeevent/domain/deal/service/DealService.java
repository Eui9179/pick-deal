package com.leui.storeevent.domain.deal.service;

import com.leui.storeevent.domain.deal.dto.DealEvent;
import com.leui.storeevent.domain.deal.dto.RemoveDealReservation;
import com.leui.storeevent.domain.deal.entity.DealReservation;
import com.leui.storeevent.domain.deal.repository.DealRepository;
import com.leui.storeevent.domain.deal.repository.DealReservationRepository;
import com.leui.storeevent.domain.prcessedevent.aop.TransactionalIdempotentEvent;
import exception.OutOfStockException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DealReservationRepository dealReservationRepository;

    @TransactionalIdempotentEvent
    public void commitStockQuantity(DealEvent event) {
        int stockQuantity = dealRepository.decreaseStockQuantity(event.dealId(), event.quantity());
        if (stockQuantity == 0) {
            if (!dealRepository.existsById(event.dealId())) {
                throw new EntityNotFoundException("Deal Not Found. dealId: " + event.dealId());
            }
            throw new OutOfStockException("Out of Stock. dealId: " + event.dealId());
        }

        dealReservationRepository.deleteByOrderId(event.orderId());

    }

    @TransactionalIdempotentEvent
    public void removeDealReservation(RemoveDealReservation removeDealReservation) {
        dealReservationRepository.deleteByOrderId(removeDealReservation.orderId());
    }

}
