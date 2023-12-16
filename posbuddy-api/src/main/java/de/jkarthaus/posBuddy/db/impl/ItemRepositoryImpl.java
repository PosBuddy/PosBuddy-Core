package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.exception.ItemNotFoundException;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Singleton
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final EntityManager entityManager;

    @ReadOnly
    @Override
    public List<ItemEntity> findByStation(String stationId) {
        TypedQuery<ItemEntity> query = entityManager.createQuery(
                "select i from items as i where i.dispensingStationId = :dispensingStation",
                ItemEntity.class
        ).setParameter("dispensingStation", stationId);
        return query.getResultList();
    }

    @ReadOnly
    @Override
    public ItemEntity findItemById(String itemId) throws ItemNotFoundException {
        try {
            TypedQuery<ItemEntity> query = entityManager.createQuery(
                    "select i from items as i where i.id = :itemId",
                    ItemEntity.class
            ).setParameter("itemId", itemId);
            return query.getSingleResult();
        } catch (NoResultException noResultException) {
            throw new ItemNotFoundException("Item:" + itemId + " not exsits");
        }
    }

}
