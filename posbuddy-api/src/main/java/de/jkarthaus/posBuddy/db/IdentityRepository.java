package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import io.micronaut.transaction.annotation.Transactional;


public interface IdentityRepository {
    IdentityEntity findById(String podBuddyId) throws posBuddyIdNotAssignedException;

    @Transactional
    void setNewBalance(String posBuddyId, Float balance);
}
