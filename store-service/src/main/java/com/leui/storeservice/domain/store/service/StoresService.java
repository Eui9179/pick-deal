package com.leui.storeservice.domain.store.service;

import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.dto.StoresRequest;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StoresService {

    private StoresRepository storesRepository;

    // TODO 위치 기반으로 가게 리턴
    public List<StoreInfoResponse> getStores(StoresRequest request) {
        return storesRepository.findAll()
                .stream()
                .map(StoreInfoResponse::from)
                .toList();
    }

    public Long updateStore(Long id, StoreUpdateRequest request) {
        Stores store = storesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Stores entity not found. id:" + id));
        store.updateContent(request);
        return id;
    }
}
