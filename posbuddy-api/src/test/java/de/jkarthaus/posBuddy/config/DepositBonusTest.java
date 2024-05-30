package de.jkarthaus.posBuddy.config;

import de.jkarthaus.posBuddy.model.config.DepositBonusConfig;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

@MicronautTest
public class DepositBonusTest {

    @Test
    void testDiscount(ObjectMapper objectMapper) throws IOException {
        DepositBonusConfig depositBonusConfig = new DepositBonusConfig(
                Arrays.asList(
                        new DepositBonusConfig.DepositBonus(
                                "5% Einzahlungsbonus",
                                true,
                                false,
                                50.0,
                                100.0,
                                5
                        ),
                        new DepositBonusConfig.DepositBonus(
                                "10% Einzahlungsbonus",
                                true,
                                false,
                                110.0,
                                -1.0,
                                10
                        )
                )
        );
        System.out.println(
                objectMapper.writeValueAsString(depositBonusConfig)
        );
    }
}
