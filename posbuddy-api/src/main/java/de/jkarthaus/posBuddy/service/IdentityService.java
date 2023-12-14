package de.jkarthaus.posBuddy.service;

import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;

public interface IdentityService {


    IdentityResponse getIdentityResponseById(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAssignedException;
}
