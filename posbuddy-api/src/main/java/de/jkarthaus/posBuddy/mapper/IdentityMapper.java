package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import jakarta.inject.Singleton;

@Singleton
public interface IdentityMapper {

    IdentityResponse toResponse(IdentityEntity identityEntity);

    IdentityEntity fromRequest(
            String posBuddyId,
            AllocatePosBuddyIdRequest allocatePosBuddyIdRequest,
            boolean isStatic);
}
