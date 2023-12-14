package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class IdentityRepositoryImpl implements IdentityRepository {

    private final EntityManager entityManager;

    @Override
    @ReadOnly
    public IdentityEntity findById(String posBuddyId) {
        TypedQuery<IdentityEntity> query = entityManager.createQuery(
                "select i from identity as i where i.posbuddyid = :posBuddyId ",

                IdentityEntity.class
        ).setParameter("posBuddyId", posBuddyId);
        return query.getSingleResult();
    }
}
