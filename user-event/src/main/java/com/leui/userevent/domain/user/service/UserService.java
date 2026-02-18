package com.leui.userevent.domain.user.service;

import com.leui.userevent.domain.prcessedevent.aop.TransactionalIdempotentEvent;
import com.leui.userevent.domain.user.dto.UserEvent;
import com.leui.userevent.domain.user.entity.User;
import com.leui.userevent.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @TransactionalIdempotentEvent
    public void applyUserPoint(UserEvent event) {
        User user = getUser(event.userId());
        user.subtractPoint(event.usedPoint()); // 포인트 사용
        BigDecimal earn = event.totalAmount().subtract(event.usedPoint()).divide(BigDecimal.TEN);
        user.accumulatePoint(earn); // 포인트 적립
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Not found. id = {}" + userId));
    }

}
