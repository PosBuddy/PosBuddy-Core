package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;


public interface IdentityRepository {
    IdentityEntity findById(String podBuddyId) throws posBuddyIdNotAssignedException;
}
