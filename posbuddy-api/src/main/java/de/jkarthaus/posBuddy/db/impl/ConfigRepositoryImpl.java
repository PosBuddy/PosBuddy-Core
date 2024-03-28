package de.jkarthaus.posBuddy.db.impl;

import de.jkarthaus.posBuddy.db.ConfigRepository;
import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Singleton
@Slf4j
public class ConfigRepositoryImpl implements ConfigRepository {

    private final EntityManager entityManager;

    @Override
    @ReadOnly
    public Optional<ConfigEntity> findById(ConfigID configID) {
        ConfigEntity result = null;
        try {
            TypedQuery<ConfigEntity> query = entityManager.createQuery(
                    """
                            select i from config as i where i.id = :configID
                            """,
                    ConfigEntity.class
            ).setParameter("configID", configID);
            result = query.getSingleResult();
        } catch (NoResultException noResultException) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    @Transactional
    public void updateConfig(ConfigEntity configEntity) {
        entityManager.merge(configEntity);
    }
}
