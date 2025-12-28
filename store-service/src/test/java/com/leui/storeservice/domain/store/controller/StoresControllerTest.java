package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoresControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testStoreSave() {
        //given
        StoreSaveRequest request = new StoreSaveRequest("name1", 1.1, 1.1, "address1", "010-1234-1234", LocalDateTime.now());

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity("/api/v1/stores", request, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(Long.class);

    }


}
