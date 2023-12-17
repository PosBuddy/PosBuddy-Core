package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.exception.ItemNotFoundException;
import de.jkarthaus.posBuddy.exception.PosBuddyIdNotAssignableException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotAssignedException;
import de.jkarthaus.posBuddy.exception.posBuddyIdNotValidException;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.model.Constants;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ServingRequest;
import de.jkarthaus.posBuddy.service.PartyActionService;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class PartyActionServiceImpl implements PartyActionService {

    private final IdentityMapper identityMapper;
    private final IdentityRepository identityRepository;
    private final RevenueRepository revenueRepository;
    private final ItemRepository itemRepository;

    @Override
    public IdentityResponse getIdentityResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAssignedException {
        if (!isIdValid(posBuddyId)) {
            throw new posBuddyIdNotValidException("no valid UUID");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity == null) {
            log.info("Actual valid posBuddy Identity with ID:{} not found in Database");
            throw new posBuddyIdNotAssignedException("posBuddy ID not found");
        }
        return identityMapper.toResponse(identityEntity);
    }

    public void serveItems(ServingRequest servingRequest, String posBuddyId)
            throws posBuddyIdNotAssignedException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        AtomicReference<Float> actBallance = new AtomicReference<>(identityEntity.getBalance());
        servingRequest.getServeItems().stream().forEach(serveItem -> {
            try {
                ItemEntity itemEntity = itemRepository.findItemById(serveItem.getItemId());
                RevenueEntity revenueEntity = new RevenueEntity();
                revenueEntity.setAmount(serveItem.getCount());
                revenueEntity.setTimeofsales(Instant.now());
                revenueEntity.setValue(Double.valueOf(serveItem.getCount() * itemEntity.getPrice()).floatValue());
                revenueEntity.setPosbuddyid(posBuddyId);
                revenueEntity.setPaymentaction(Constants.PAYMENT);
                revenueRepository.addRevenue(revenueEntity);
                actBallance.set(actBallance.get() - revenueEntity.getValue());
            } catch (ItemNotFoundException e) {
                log.error("Item with id:{} not exists. -> no revenue", serveItem.getItemId());
            }
            identityRepository.setNewBalance(serveItem.getItemId(), actBallance.get());
        });

    }


    public void addDeposit(String posBuddyId, float value) throws posBuddyIdNotAssignedException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        float newBalance = identityEntity.getBalance() + value;
        // add revenue
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setAmount(1);
        revenueEntity.setTimeofsales(Instant.now());
        revenueEntity.setValue(value);
        revenueEntity.setPosbuddyid(posBuddyId);
        revenueEntity.setPaymentaction(Constants.DEPOSIT);
        revenueRepository.addRevenue(revenueEntity);
        // setNewBallance
        identityRepository.setNewBalance(identityEntity.getPosbuddyid(), newBalance);
    }

    @Override
    public void allocatePosBuddyId(AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAssignableException, posBuddyIdNotValidException {
        if (!isIdValid(allocatePosBuddyIdRequest.getPosBuddyId())) {
            throw new posBuddyIdNotValidException("");
        }
        boolean isAssignable = identityRepository.isPosBuddyIdAssignable(allocatePosBuddyIdRequest.getPosBuddyId());
        if (isAssignable) {
            identityRepository.AssignPosBuddyId(identityMapper.fromRequest(allocatePosBuddyIdRequest));
            return;
        }
        throw new PosBuddyIdNotAssignableException("Id is not asignable");
    }

    public void deAllocatePosBuddyId(String posBuddyId) {

    }

    private boolean isIdValid(String posBuddyId) {
        try {
            UUID uuid = UUID.fromString(posBuddyId);
        } catch (Exception exception) {
            log.error("posBuddy Id: {} is not a valif UUID", posBuddyId);
            return false;
        }
        return true;
    }

}
