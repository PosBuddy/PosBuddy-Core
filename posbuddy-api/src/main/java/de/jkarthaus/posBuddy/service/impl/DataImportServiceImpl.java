package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.DispensingStationRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.exception.ParseImportException;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Slf4j
@RequiredArgsConstructor
public class DataImportServiceImpl implements de.jkarthaus.posBuddy.service.DataImportService {

    private final ItemRepository itemRepository;
    private final DispensingStationRepository dispensingStationRepository;

    private Map<String, ItemDataImport> itemDataImportMap;
    private Map<String, DispensingStationDataImport> dispensingStationDataImportMap;


    @Override
    public void importItemCsv() throws ParseImportException {
        itemDataImportMap = new HashMap<>();
        log.info("check all data for import");
        try {
            Reader in = new FileReader("/tmp/posBuddyItems.csv");
            Iterable<CSVRecord> records = CSVFormat
                    .EXCEL
                    .withHeader("ID", "Bezeichnung", "Einheit", "Mindestalter", "Ausgabestation", "Preis")
                    .withFirstRecordAsHeader()
                    .parse(in);
            for (CSVRecord record : records) {
                String itemId = checkItemId(record.get("ID"));
                itemDataImportMap.put(
                        itemId,
                        new ItemDataImport(
                                checkItemText(record.get("Bezeichnung"), itemId),
                                checkItemUnit(record.get("Einheit"), itemId),
                                checkItemMinAge(record.get("Mindestalter"), itemId),
                                checkDispensingStation(record.get("Ausgabestation"), itemId),
                                checkPrice(record.get("Preis"), itemId)
                        )
                );
            }
        } catch (Exception e) {
            throw new ParseImportException(e.getMessage());
        }
        log.info("remove all items");
        itemRepository.clearItems();
        log.info("add new items from import");
        itemDataImportMap.keySet().stream().forEach(itemId ->
                itemRepository.addItem(
                        new ItemEntity(
                                itemId,
                                itemDataImportMap.get(itemId).itemText,
                                itemDataImportMap.get(itemId).getUnit(),
                                itemDataImportMap.get(itemId).getMinAge(),
                                itemDataImportMap.get(itemId).getDispensingStation(),
                                Float.valueOf(itemDataImportMap.get(itemId).getPrice()).doubleValue()
                        )
                )
        );
    }


    @Override
    public void importDispensingStationCsv() throws ParseImportException {
        dispensingStationDataImportMap = new HashMap<>();
        log.info("check all data for import");
        try {
            Reader in = new FileReader("/tmp/posBuddyDispensingStations.csv");
            Iterable<CSVRecord> records = CSVFormat
                    .EXCEL
                    .withHeader("ID", "Bezeichnung", "Ort")
                    .withFirstRecordAsHeader()
                    .parse(in);
            for (CSVRecord record : records) {
                String dissId = checkDispensingStationId(record.get("ID"));
                dispensingStationDataImportMap.put(
                        dissId,
                        new DispensingStationDataImport(
                                checkDispensingStationName(record.get("Bezeichnung"), dissId),
                                checkDispensingStationLocation(record.get("Ort"), dissId)
                        )
                );
            }
        } catch (Exception e) {
            throw new ParseImportException(e.getMessage());
        }
        log.info("remove all Dispensing Stations");
        dispensingStationRepository.clearDispensingStations();
        log.info("add new Dispensing Stations from import");
        dispensingStationDataImportMap.keySet().stream().forEach(dissId ->
                dispensingStationRepository.addDispensingStation(
                        new DispensingStationEntity(
                                dissId,
                                dispensingStationDataImportMap.get(dissId).name,
                                dispensingStationDataImportMap.get(dissId).location
                        )
                )
        );
    }

    private String checkItemId(String itemId) throws ParseImportException {
        if (itemId.trim().length() > 10) {
            throw new ParseImportException(
                    "length of ItemId:" + itemId + " > 10"
            );
        }
        if (itemDataImportMap.containsKey(itemId)) {
            throw new ParseImportException(
                    "ItemId:" + itemId + " is not unique"
            );
        }
        return itemId.trim();
    }

    private String checkDispensingStationId(String dispensingStationId) throws ParseImportException {
        if (dispensingStationId.trim().length() > 10) {
            throw new ParseImportException(
                    "length of dispensingStationId:" + dispensingStationId + " > 10"
            );
        }
        if (dispensingStationDataImportMap.containsKey(dispensingStationId)) {
            throw new ParseImportException(
                    "ItemId:" + dispensingStationId + " is not unique"
            );
        }
        return dispensingStationId.trim();
    }

    private String checkItemText(String itemText, String itemId) throws ParseImportException {
        if (itemText == null || itemText.trim().isEmpty()) {
            throw new ParseImportException(
                    "Text of Item:" + itemId + " is empty or to short"
            );
        }
        if (itemText.length() > 40) {
            throw new ParseImportException(
                    "length of ItemId:" + itemId + " > 40"
            );
        }
        return itemText.trim();
    }

    private String checkDispensingStationName(String name, String dissId) throws ParseImportException {
        if (name == null || name.trim().isEmpty()) {
            throw new ParseImportException(
                    "Text of Dispensing Station Name:" + dissId + " is empty or to short"
            );
        }
        if (name.length() > 40) {
            throw new ParseImportException(
                    "length of Dispensing Station Name:" + dissId + " > 40"
            );
        }
        return name.trim();
    }

    private String checkDispensingStationLocation(String location, String dissId) throws ParseImportException {
        if (location == null || location.trim().isEmpty()) {
            throw new ParseImportException(
                    "Text of Dispensing Station Location:" + dissId + " is empty or to short"
            );
        }
        if (location.length() > 40) {
            throw new ParseImportException(
                    "length of Dispensing Station Loation:" + dissId + " > 40"
            );
        }
        return location.trim();
    }

    private String checkItemUnit(String itemUnit, String itemId) throws ParseImportException {
        if (itemUnit == null || itemUnit.trim().isEmpty()) {
            throw new ParseImportException(
                    "Text of Item: " + itemId + " is empty or to short"
            );
        }
        if (itemUnit.length() > 20) {
            throw new ParseImportException(
                    "length of ItemUnit:" + itemUnit + " > 20 at id:" + itemId
            );
        }
        return itemUnit.trim();
    }

    private int checkItemMinAge(String itemMinAge, String itemId) throws ParseImportException {
        int check;
        try {
            check = Integer.parseInt(itemMinAge);
        } catch (Exception e) {
            throw new ParseImportException(
                    "itemMinAge of item:" + itemId + " is not a number"
            );
        }
        if (check < 0 || check > 99) {
            throw new ParseImportException(
                    "itemMinAge of item:" + itemId + " is not a number between 0 and 99"
            );
        }
        return check;
    }

    private float checkPrice(String price, String itemId) throws ParseImportException {
        float priceF;
        try {
            price = price.replace("EUR", "");
            price = price.replace(",", ".");
            price = price.trim();
            priceF = Float.parseFloat(price);
        } catch (Exception e) {
            throw new ParseImportException(
                    "Price of item:" + itemId + " is not a valid number"
            );
        }
        if (priceF < 0) {
            throw new ParseImportException(
                    "Price of item:" + itemId + " mus be a positive number"
            );
        }
        return priceF;
    }

    private String checkDispensingStation(String dispensingStation, String itemId) throws ParseImportException {
        if (dispensingStation == null || dispensingStation.trim().isEmpty()) {
            throw new ParseImportException(
                    "Dispensing Station of Item :" + itemId + " is empty or to short"
            );
        }

        if (dispensingStation.length() > 10) {
            throw new ParseImportException(
                    "length of dispensingStation:" + dispensingStation + " > 10 at id:" + itemId
            );
        }
        return dispensingStation.trim();
    }


    @Data
    @AllArgsConstructor
    private static class ItemDataImport {
        String itemText;
        String unit;
        int minAge;
        String dispensingStation;
        float price;
    }

    @Data
    @AllArgsConstructor
    private static class DispensingStationDataImport {
        String name;
        String location;
    }


}
