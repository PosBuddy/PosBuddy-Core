package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Singleton
public class IdentityRepositoryImpl implements IdentityRepository {

    private final EntityManager entityManager;

    @Override
    @ReadOnly
    public IdentityEntity findById(String posBuddyId) throws posBuddyIdNotAssignedException {
        IdentityEntity result = null;
        try {
            TypedQuery<IdentityEntity> query = entityManager.createQuery(
                    """
                                                    
                                select i from identity as i where i.posbuddyid = :posBuddyId
                            and i.startallocation < now() 
                            and i.endallocation is null
                               
                            """,
                    IdentityEntity.class
            ).setParameter("posBuddyId", posBuddyId);
            result = query.getSingleResult();
        } catch (NoResultException noResultException) {
            throw new posBuddyIdNotAssignedException("ID is not assigned");
        }
        return result;
    }

    @Transactional
    @Override
    public void setNewBalance(String posBuddyId, Float balance) {
        IdentityEntity identityEntity = new IdentityEntity();
        identityEntity.setPosbuddyid(posBuddyId);
        identityEntity.setBalance(balance);
        entityManager.merge(identityEntity);
    }

}
