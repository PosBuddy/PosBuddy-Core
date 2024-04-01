package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.tools.Tools;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Singleton
@Slf4j
public class IdentityMapperImpl implements IdentityMapper {
    @Override
    public IdentityResponse toResponse(IdentityEntity identityEntity) {
        return new IdentityResponse(
                identityEntity.getPosbuddyid(),
                identityEntity.getSurname(),
                identityEntity.getLastname(),
                Tools.isAgeUnderYouthProtection(
                        identityEntity.getBirthday(),
                        identityEntity.getPosbuddyid()
                ),
                identityEntity.getAtribute1(),
                identityEntity.getAtribute2(),
                identityEntity.getAtribute3(),
                identityEntity.getBalance()
        );
    }

    @Override
    public IdentityEntity fromRequest(String posBuddyId, AllocatePosBuddyIdRequest allocatePosBuddyIdRequest) {
        System.out.println(ZonedDateTime.now());
        return new IdentityEntity(
                posBuddyId,
                allocatePosBuddyIdRequest.getSurname(),
                allocatePosBuddyIdRequest.getLastname(),
                allocatePosBuddyIdRequest.getBirthday(),
                allocatePosBuddyIdRequest.getAttribute1(),
                allocatePosBuddyIdRequest.getAttribute2(),
                allocatePosBuddyIdRequest.getAttribute3(),
                LocalDateTime.now(),
                null,
                false,
                allocatePosBuddyIdRequest.getBalance()
        );
    }


}
