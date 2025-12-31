package com.leui.storeservice.domain.discountpolicy.service;

import com.leui.storeservice.domain.discountpolicy.repository.DiscountPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DiscountPolicyService {

    private final DiscountPolicyRepository discountPolicyRepository;



}
