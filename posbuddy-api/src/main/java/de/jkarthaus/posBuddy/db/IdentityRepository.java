package de.jkarthaus.posBuddy.db;

import de.jkarthaus.posBuddy.db.entities.IdentityEntity;


public interface IdentityRepository {
    IdentityEntity findById(String podBuddyId);
}
