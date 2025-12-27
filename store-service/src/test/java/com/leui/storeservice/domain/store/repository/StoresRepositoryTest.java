package com.leui.storeservice.domain.store.repository;

import com.leui.storeservice.domain.store.entity.Stores;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres")
class StoresRepositoryTest {

    @Autowired
    StoresRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        // 1. radius = 100m
        repository.save(Stores.create("name1_100", getPoint(126.9725445, 37.5557536), "address1_100", "phoneNumber1", LocalDateTime.now()));
        repository.save(Stores.create("name2_100", getPoint(126.9736745, 37.5548556), "address2_100", "phoneNumber2", LocalDateTime.now()));
        repository.save(Stores.create("name3_100", getPoint(126.9717455, 37.5542206), "address3_100", "phoneNumber3", LocalDateTime.now()));

        // 2. radius = 200m
        repository.save(Stores.create("name1_200", getPoint(126.9725445, 37.5566520), "address1_200", "phoneNumber4", LocalDateTime.now()));
        repository.save(Stores.create("name2_200", getPoint(126.9748045, 37.5548556), "address2_200", "phoneNumber5", LocalDateTime.now()));
        repository.save(Stores.create("name3_200", getPoint(126.9711460, 37.5535900), "address3_200", "phoneNumber6", LocalDateTime.now()));

        // 3. radius = 300
        repository.save(Stores.create("name1_300", getPoint(126.9725445, 37.5575500), "address1_300", "phoneNumber7", LocalDateTime.now()));
    }

    @Test
    void findNear_radius100() {
        //given
        double x = 126.9725445;
        double y = 37.5548556;
        int radius = 100;

        //when
        List<Stores> near = repository.findNear(x, y, radius);

        //then
        assertThat(near)
                .extracting(Stores::getName)
                .containsExactlyInAnyOrder(
                        "name1_100",
                        "name2_100",
                        "name3_100"
                );
        assertThat(near)
                .extracting(Stores::getName)
                .doesNotContain("name1_300");
    }

    @Test
    void findNear_radius200() {
        //given
        double x = 126.9725445;
        double y = 37.5548556;
        int radius = 200;

        //when
        List<Stores> near = repository.findNear(x, y, radius);

        //then
        assertThat(near)
                .extracting(Stores::getName)
                .containsExactlyInAnyOrder(
                        "name1_100",
                        "name2_100",
                        "name3_100",
                        "name1_200",
                        "name2_200",
                        "name3_200"
                );

    }


    private Point getPoint(double x, double y) {
        return new GeometryFactory().createPoint(new Coordinate(x, y));
    }
}
