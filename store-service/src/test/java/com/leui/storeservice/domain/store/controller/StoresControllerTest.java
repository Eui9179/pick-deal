package com.leui.storeservice.domain.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StoresControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // 1. radius = 100

        // 2. radius = 200

        // 3. radius = 300

    }


}
