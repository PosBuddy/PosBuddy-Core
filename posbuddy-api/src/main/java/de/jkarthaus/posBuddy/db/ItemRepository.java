package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.exception.ItemNotFoundException;
import io.micronaut.transaction.annotation.ReadOnly;

import java.util.List;


public interface ItemRepository {
    List<ItemEntity> findByStation(String stationId);

    @ReadOnly
    ItemEntity findItemById(String itemId) throws ItemNotFoundException;
}
