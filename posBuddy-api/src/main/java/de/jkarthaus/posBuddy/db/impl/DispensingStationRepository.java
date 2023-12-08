package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import io.micronaut.transaction.annotation.ReadOnly;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DispensingStationRepository implements de.jkarthaus.posBuddy.db.DispensingStationRepository {

    private final EntityManager entityManager;

    @Override
    @ReadOnly
    public List<DispensingStationEntity> getDispensingStations() {
        TypedQuery<DispensingStationEntity> query = entityManager.createQuery(
                "select d from  dispensingStations as d ",
                DispensingStationEntity.class
        );
        return query.getResultList();
    }
}
