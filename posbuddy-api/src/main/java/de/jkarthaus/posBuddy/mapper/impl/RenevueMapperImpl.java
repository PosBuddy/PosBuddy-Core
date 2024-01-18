package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.mapper.RevenueMapper;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class RenevueMapperImpl implements RevenueMapper {
    @Override
    public RevenueResponse toResponse(List<RevenueEntity> revenueEntities) {
        return new RevenueResponse(
                revenueEntities.stream().map(revenueEntity -> {
                            return new RevenueResponse.RevenueEntry(
                                    revenueEntity.getItemtext(),
                                    revenueEntity.getAmount(),
                                    revenueEntity.getValue(),
                                    revenueEntity.getPaymentaction(),
                                    revenueEntity.getTimeofaction()
                            );
                        }
                ).collect(Collectors.toList())
        );
    }
}
