package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.ItemEntity;

import java.util.List;


public interface ItemRepository {
    List<ItemEntity> findByStation(String stationId);
}
