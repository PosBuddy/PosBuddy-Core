package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Singleton
@Slf4j
public class IdentityRepositoryImpl implements IdentityRepository {

    private final EntityManager entityManager;

    @Override
    @ReadOnly
    public IdentityEntity findById(String posBuddyId) throws posBuddyIdNotAllocatedException {
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
            throw new posBuddyIdNotAllocatedException("ID is not assigned");
        }
        return result;
    }

    @Override
    @ReadOnly
    public boolean isPosBuddyIdAllocatable(String posBuddyId) {
        IdentityEntity identityEntity = null;
        try {
            TypedQuery<IdentityEntity> query = entityManager.createQuery(
                    """
                            select i from identity as i where i.posbuddyid = :posBuddyId
                            """,
                    IdentityEntity.class
            ).setParameter("posBuddyId", posBuddyId);
            identityEntity = query.getSingleResult();
        } catch (NoResultException noResultException) {
            log.debug("id:{} is assignable becuase id not exists");
            return true;
        }
        if (identityEntity.getEndallocation() == null) {
            log.debug("id:{} already assigned", posBuddyId);
            return false;
        }
        if (identityEntity.getEndallocation().isBefore(LocalDateTime.now())) {
            log.debug("id:{} EndAllocation is before now -> id is new Assignable");
            return true;
        }
        log.warn("id:{} has unknown condition -> set to not assignable");
        return false;
    }

    @Transactional
    @Override
    public void setNewBalance(String posBuddyId, Float balance) {
        IdentityEntity identityEntity = new IdentityEntity();
        identityEntity.setPosbuddyid(posBuddyId);
        identityEntity.setBalance(balance);
        entityManager.merge(identityEntity);
    }


    @Transactional
    @Override
    public void deAllocatePosBuddyId(IdentityEntity identityEntity) {
        identityEntity.setEndallocation(LocalDateTime.now());
        entityManager.merge(identityEntity);
        }

    @Transactional
    @Override
    public void allocatePosBuddyId(IdentityEntity identityEntity) {
        entityManager.merge(identityEntity);
    }
}
