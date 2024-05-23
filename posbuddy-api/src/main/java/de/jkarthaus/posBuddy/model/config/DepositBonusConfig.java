package de.jkarthaus.posBuddy.model.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Serdeable
@JsonInclude(JsonInclude.Include.ALWAYS)
public class DepositBonusConfig {
    public List<DepositBonus> depositBonusList = new ArrayList<>();

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Serdeable
    public static class DepositBonus {
        String revenueText = "";
        boolean activeOnStaticId = false;
        boolean activeOnVolatileId = false;
        Double fromAmount = 0.0;
        Double toAmount = 0.0;
        Integer creditNotePercent = 0;
    }

}
