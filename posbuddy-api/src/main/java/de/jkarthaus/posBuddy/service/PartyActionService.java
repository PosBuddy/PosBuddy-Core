package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.model.gui.*;

import java.io.IOException;
import java.util.List;

public interface PartyActionService {


    IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;

    List<RevenueResponse> getRevenueResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;


    void serveItems(List<ServeItem> servItems, String posBuddyId)
            throws posBuddyIdNotAllocatedException, OutOfBalanceException;

    void addDeposit(String posBuddyId, Float value) throws posBuddyIdNotAllocatedException, IOException;

    void allocatePosBuddyId(String posBuddyId, AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAllocateableException, posBuddyIdNotValidException;

    List<DispensingStationResponse> getDispensingStations();

    void deAllocatePosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException, OutOfBalanceException, PosBuddyIdStaticException;

    void payout(String posBuddyId) throws
            posBuddyIdNotAllocatedException, OutOfBalanceException;


    IdDataResponse getIdDataResponse(String posBuddyId) throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;
}
