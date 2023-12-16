package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import io.micronaut.transaction.annotation.Transactional;

public interface RevenueRepository {

    @Transactional
    void addRevenue(RevenueEntity revenueEntity);
}
