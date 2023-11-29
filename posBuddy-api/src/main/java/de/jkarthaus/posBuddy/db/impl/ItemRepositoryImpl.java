package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.entities.Item;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Singleton
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final EntityManager entityManager;

    @ReadOnly
    @Override
    public List<Item> findByStation(String stationId) {
        TypedQuery<Item> query = entityManager.createQuery(
                        "select i from items as i where station= :station",
                        Item.class
                )
                .setParameter("station", stationId);
        return null;
    }
}
