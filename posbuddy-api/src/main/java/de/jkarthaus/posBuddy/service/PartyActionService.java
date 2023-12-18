package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.PosBuddyIdNotAssignableException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ServingRequest;

public interface PartyActionService {


    IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAssignedException;

    void serveItems(ServingRequest servingRequest, String posBuddyId)
            throws posBuddyIdNotAssignedException;

    void addDeposit(String posBuddyId, float value) throws posBuddyIdNotAssignedException;

    void allocatePosBuddyId(AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAssignableException, posBuddyIdNotValidException;
}
