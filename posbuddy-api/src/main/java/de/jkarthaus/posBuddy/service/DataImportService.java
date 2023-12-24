package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.ParseImportException;

public interface DataImportService {
    void importItemCsv() throws ParseImportException;

    void importDispensingStationCsv() throws ParseImportException;
}
