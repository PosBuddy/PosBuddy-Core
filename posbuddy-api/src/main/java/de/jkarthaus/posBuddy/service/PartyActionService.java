package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;

public interface PartyActionService {


    IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAssignedException;
}
