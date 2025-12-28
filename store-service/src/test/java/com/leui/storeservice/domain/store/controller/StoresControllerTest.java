package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("postgres")
public class StoresControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testStoreSave() {
        //given
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        StoreSaveRequest saveRequest = new StoreSaveRequest("name1", 1.1, 1.1, "address1",
                "010-1234-1234", LocalDateTime.now());
        HttpEntity<StoreSaveRequest> data = new HttpEntity<>(saveRequest);
        body.add("data", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity("/api/v1/stores", request, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isInstanceOf(Long.class);

    }
}
