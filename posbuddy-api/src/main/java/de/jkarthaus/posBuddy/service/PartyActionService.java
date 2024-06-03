package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.model.gui.*;
import io.micronaut.transaction.annotation.Transactional;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PartyActionService {


    @Transactional
    void doSpecialRevenue(
            String posBuddyId,
            String operation,
            Double value,
            String itemText) throws posBuddyIdNotValidException,
            posBuddyIdNotAllocatedException,
            OutOfBalanceException, ActionNotSupportetException;

    IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;

    List<RevenueResponse> getRevenueResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;


    void serveItems(List<ServeItem> servItems, String posBuddyId)
            throws posBuddyIdNotAllocatedException, OutOfBalanceException;

    void addDeposit(String posBuddyId, Float value) throws posBuddyIdNotAllocatedException, IOException;


    void allocateOneTimePosBuddyId(AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAllocateableException, posBuddyIdNotValidException, JRException, SQLException, IOException;

    void allocatePosBuddyId(
            String posBuddyId,
            AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            boolean isStatic
    ) throws PosBuddyIdNotAllocateableException, posBuddyIdNotValidException;

    List<DispensingStationResponse> getDispensingStations();

    void deAllocatePosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException, OutOfBalanceException, PosBuddyIdStaticException;

    void payout(String posBuddyId) throws
            posBuddyIdNotAllocatedException, OutOfBalanceException;


    IdDataResponse getIdDataResponse(String posBuddyId) throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;
}
