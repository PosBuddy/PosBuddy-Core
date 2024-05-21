package de.jkarthaus.posBuddy.config;

import de.jkarthaus.posBuddy.model.config.DiscountConfig;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

@MicronautTest
public class DiscountTest {

    @Test
    void testDiscount(ObjectMapper objectMapper) throws IOException {
        DiscountConfig discountConfig = new DiscountConfig(
                Arrays.asList(
                        new DiscountConfig.Discount(
                                true,
                                false,
                                50.0,
                                100.0,
                                5
                        ),
                        new DiscountConfig.Discount(
                                true,
                                false,
                                110.0,
                                -1.0,
                                10
                        )
                )
        );
        System.out.println(
                objectMapper.writeValueAsString(discountConfig)
        );
    }
}
