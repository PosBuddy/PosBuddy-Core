package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.mapper.RevenueMapper;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class RevenueMapperImpl implements RevenueMapper {
    @Override
    public List<RevenueResponse> toResponse(List<RevenueEntity> revenueEntities) {
        return revenueEntities.stream().map(revenueEntity -> {
                    return new RevenueResponse(
                            revenueEntity.getItemtext(),
                            revenueEntity.getAmount(),
                            revenueEntity.getValue(),
                            revenueEntity.getPaymentaction(),
                            revenueEntity.getTimeofaction()
                    );
                }
        ).collect(Collectors.toList());
    }
}
