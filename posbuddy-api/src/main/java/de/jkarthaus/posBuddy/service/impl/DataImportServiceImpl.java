package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.ItemRepository;
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
public class DataImportServiceImpl {

    private final ItemRepository itemRepository;

    private Map<String, ItemImport> dataImport;

    private void importItemCsv() throws ParseImportException {
        dataImport = new HashMap<>();
        try {
            Reader in = new FileReader("path/to/file.csv");
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            for (CSVRecord record : records) {
                String itemId = checkId(record.get("ID"));
                dataImport.put(
                        itemId,
                        new ItemImport(
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
    }

    private String checkId(String itemId) throws ParseImportException {
        if (itemId.length() > 10) {
            throw new ParseImportException(
                    "length of ItemId:" + itemId + " > 10"
            );
        }
        if (dataImport.containsKey(itemId)) {
            throw new ParseImportException(
                    "ItemId:" + itemId + " is not unique"
            );
        }
        return itemId;
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
        return  check;
    }

    private float checkPrice(String price, String itemId) throws ParseImportException {
        float priceF;
        try {
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


    private void importDispensingStationCsv() {
    }

    @Data
    @AllArgsConstructor
    private static class ItemImport {
        String itemText;
        String unit;
        int minAge;
        String dispensingStation;
        float price;
    }


}
