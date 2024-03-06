package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.OutOfBalanceException;
import de.jkarthaus.posBuddy.exception.PosBuddyIdNotAllocateableException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAllocatedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import de.jkarthaus.posBuddy.model.gui.ServeItem;

import java.util.List;

public interface PartyActionService {


    IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;

    List<RevenueResponse> getRevenueResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException;


    void serveItems(List<ServeItem> servItems, String posBuddyId)
            throws posBuddyIdNotAllocatedException, OutOfBalanceException;

    void addDeposit(String posBuddyId, float value) throws posBuddyIdNotAllocatedException;

    void allocatePosBuddyId(String posBuddyId, AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAllocateableException, posBuddyIdNotValidException;

    void deAllocatePosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException, OutOfBalanceException;

    void payout(String posBuddyId) throws
            posBuddyIdNotAllocatedException, OutOfBalanceException;


}
