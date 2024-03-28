package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.Transactional;

import java.util.Optional;


public interface ConfigRepository {
    @ReadOnly
    Optional<ConfigEntity> findById(ConfigID configID);

    @Transactional
    void updateConfig(ConfigEntity configEntity);
}
