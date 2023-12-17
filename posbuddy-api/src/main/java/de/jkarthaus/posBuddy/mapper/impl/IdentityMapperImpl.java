package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Singleton
@Slf4j
public class IdentityMapperImpl implements IdentityMapper {
    @Override
    public IdentityResponse toResponse(IdentityEntity identityEntity) {
        return new IdentityResponse(
                identityEntity.getPosbuddyid(),
                identityEntity.getSurname(),
                identityEntity.getLastname(),
                isAgeUnderYouthProtection(identityEntity.getBirthday(), identityEntity.getPosbuddyid()),
                identityEntity.getAtribute1(),
                identityEntity.getAtribute2(),
                identityEntity.getAtribute3(),
                identityEntity.getBalance()
        );
    }

    @Override
    public IdentityEntity fromRequest(AllocatePosBuddyIdRequest allocatePosBuddyIdRequest) {
        return new IdentityEntity(
                allocatePosBuddyIdRequest.getPosBuddyId(),
                allocatePosBuddyIdRequest.getSurname(),
                allocatePosBuddyIdRequest.getLastname(),
                allocatePosBuddyIdRequest.getBirthday(),
                allocatePosBuddyIdRequest.getAttribute1(),
                allocatePosBuddyIdRequest.getAttribute2(),
                allocatePosBuddyIdRequest.getAttribute3(),
                LocalDate.now(),
                null,
                allocatePosBuddyIdRequest.getBalance()
        );
    }

    private boolean isAgeUnderYouthProtection(LocalDate birthday, String posBuddyId) {
        if (birthday == null) {
            log.warn("cannot calculate age of posBuddyId:{} because birthday is null");
            return false;
        }
        return birthday.plusYears(16).isAfter(LocalDate.now());
    }

}
