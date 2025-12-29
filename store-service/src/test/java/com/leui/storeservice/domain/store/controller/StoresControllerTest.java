package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.entity.StoreCategory;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoreCategoryRepository;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import com.leui.storeservice.config.postgres.PostgreSQLTestContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@PostgreSQLTestContainer
public class StoresControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    StoresRepository storesRepository;

    @Autowired
    StoreCategoryRepository storeCategoryRepository;

    StoreCategory category;

    @BeforeEach
    void setUp() {
        storesRepository.deleteAll();
        storeCategoryRepository.deleteAll();
        category = storeCategoryRepository.save(StoreCategory.create("test code", "test desc"));
    }

    @Test
    void testStoreSave() {
        //given
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        StoreSaveRequest saveRequest = new StoreSaveRequest(
                "name1",
                1.1,
                1.1,
                "address1",
                "010-1234-1234",
                LocalDateTime.now(),
                category.getId());

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

    @Test
    void testStoreFindNear() {
        //given
        // radius 100
        storesRepository.save(new Stores("name1_100", LocationUtils.createPoint(126.9725445, 37.5557536),
                "address1_100", "phoneNumber1", LocalDateTime.now(), category));
        storesRepository.save(new Stores("name2_100", LocationUtils.createPoint(126.9736745, 37.5548556),
                "address2_100", "phoneNumber2", LocalDateTime.now(), category));
        storesRepository.save(new Stores("name3_100", LocationUtils.createPoint(126.9717455, 37.5542206),
                "address3_100", "phoneNumber3", LocalDateTime.now(), category));

        // radius 200
        storesRepository.save(new Stores("name1_200", LocationUtils.createPoint(126.9725445, 37.5566520),
                "address1_200", "phoneNumber4", LocalDateTime.now(), category));

        URI uri = UriComponentsBuilder
                .fromPath("/api/v1/stores")
                .queryParam("x", 126.9725445)
                .queryParam("y", 37.5548556)
                .queryParam("radius", 100)
                .build().toUri();

        //when
        ResponseEntity<List<StoreInfoResponse>> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isEqualTo(3);
    }
}
