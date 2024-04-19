package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface RevenueRepository {

    @Transactional
    void addRevenue(RevenueEntity revenueEntity);

    @ReadOnly
    List<RevenueEntity> getRevenuesByIdSince(String posBuddyId, LocalDateTime since);

    @ReadOnly
    List<RevenueEntity> getRevenuesByIdDescending(String posBuddyId, LocalDateTime since);
}
