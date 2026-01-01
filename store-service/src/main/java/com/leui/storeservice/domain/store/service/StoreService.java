package com.leui.storeservice.domain.store.service;

import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreCategoryRepository storeCategoryRepository;

    public List<StoreInfoResponse> getNearStores(StoreFindRequest request) {
        return storeRepository.findNear(request.x(), request.y(), request.radius())
                .stream()
                .map(StoreInfoResponse::from)
                .toList();
    }

    public Long updateStore(Long id, StoreUpdateRequest request) {
        Store store = getStore(id);
        store.updateContent(request);
        return id;
    }

    public Store create(StoreSaveRequest request) {
        StoreCategory category = storeCategoryRepository.getReferenceById(request.categoryId());
        return storeRepository.save(new Store(request, category));
    }

    public StoreInfoResponse getStoreInfo(Long id) {
        return StoreInfoResponse.from(getStore(id));
    }

    public Store getStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stores entity not found. id:" + id));
    }
}
