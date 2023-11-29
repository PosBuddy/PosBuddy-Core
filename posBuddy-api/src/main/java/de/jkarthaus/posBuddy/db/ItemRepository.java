package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.Item;

import java.util.List;


public interface ItemRepository {
    List<Item> findByStation(String stationId);
}
