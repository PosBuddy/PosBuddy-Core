package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.service.IdentityService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class IdentityServiceImpl implements IdentityService {

    private final IdentityMapper identityMapper;
    private final IdentityRepository identityRepository;

    @Override
    public IdentityResponse getIdentityResponseById(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAssignedException {
        try {
            UUID uuid = UUID.fromString(posBuddyId);
        } catch (Exception exception) {
            log.error("posBuddy Id: {} is not a valif UUID", posBuddyId);
            throw new posBuddyIdNotValidException("no valid UUID");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity == null) {
            log.info("Actual valid posBuddy Identity with ID:{} not found in Database");
            throw new posBuddyIdNotAssignedException("posBuddy ID not found");
        }
        return identityMapper.toResponse(identityEntity);
    }

}
