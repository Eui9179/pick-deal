package com.leui.storeservice.domain.store.service;

import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoresService {

    private final StoresRepository storesRepository;
    private final StoreCategoryRepository storeCategoryRepository;

    public List<StoreInfoResponse> getNearStores(StoreFindRequest request) {
        return storesRepository.findNear(request.x(), request.y(), request.radius())
                .stream()
                .map(StoreInfoResponse::from)
                .toList();
    }

    public Long updateStore(Long id, StoreUpdateRequest request) {
        Stores store = getStore(id);
        store.updateContent(request);
        return id;
    }

    public Long saveStore(StoreSaveRequest request) {
        StoreCategory category = storeCategoryRepository.getReferenceById(request.categoryId());
        return storesRepository.save(new Stores(request, category)).getId();
    }

    public StoreInfoResponse getStoreInfo(Long id) {
        return StoreInfoResponse.from(getStore(id));
    }

    private Stores getStore(Long id) {
        return storesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stores entity not found. id:" + id));
    }
}
