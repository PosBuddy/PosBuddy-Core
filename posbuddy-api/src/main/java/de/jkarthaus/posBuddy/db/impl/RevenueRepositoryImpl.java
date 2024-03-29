package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Singleton
@RequiredArgsConstructor
public class RevenueRepositoryImpl implements RevenueRepository {

    private final EntityManager entityManager;


    @Transactional
    @Override
    public void addRevenue(RevenueEntity revenueEntity) {
        entityManager.persist(revenueEntity);

    }

    @Override
    @ReadOnly
    public List<RevenueEntity> getRevenuesByIdSince(String posBuddyId, LocalDateTime since) {
        TypedQuery<RevenueEntity> query = entityManager.createQuery(
                        """
                                select r from revenues as r
                                where r.posbuddyid = :posBuddyId
                                and r.timeofaction > :since
                                order by r.timeofaction
                                """, RevenueEntity.class
                )
                .setParameter("posBuddyId", posBuddyId)
                .setParameter("since", since);
        return query.getResultList();
    }

    @Override
    @ReadOnly
    public List<RevenueEntity> getRevenuesByIdDescending(String posBuddyId) {
        TypedQuery<RevenueEntity> query = entityManager.createQuery(
                """
                        select r from revenues as r
                        where r.posbuddyid = :posBuddyId
                        order by r.timeofaction desc
                        """,
                RevenueEntity.class
        ).setParameter("posBuddyId", posBuddyId);

        return query.getResultList();
    }

}
