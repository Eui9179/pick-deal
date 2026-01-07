package com.leui.storeservice.common.facade;

import com.leui.storeservice.domain.deal.dto.DealCreateRequest;
import com.leui.storeservice.domain.deal.dto.DealCreateResponse;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.service.DealService;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class StoreDealFacade {

    private final StoreService storeService;
    private final DealService dealService;

    @Transactional
    public DealCreateResponse createDeal(Long storeId, DealCreateRequest request) {
        Store store = storeService.getStore(storeId);

        Deal deal = new Deal(store, request);
        DiscountPolicy policy = new DiscountPolicy(deal, request.discountPolicy());
        deal.setDiscountPolicy(policy);

        return new DealCreateResponse(dealService.create(deal).getId());
    }

}
