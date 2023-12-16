package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Singleton
@RequiredArgsConstructor
public class RevenueRepositoryImpl implements RevenueRepository {

    private final EntityManager entityManager;


    @Transactional
    @Override
    public void addRevenue(RevenueEntity revenueEntity) {
        entityManager.persist(revenueEntity);

    }
}
