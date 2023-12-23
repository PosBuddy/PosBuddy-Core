package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.model.Constants;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.ServingRequest;
import de.jkarthaus.posBuddy.service.PartyActionService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("no valid UUID");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity == null) {
            log.info("Actual valid posBuddy Identity with ID:{} not found in Database", posBuddyId);
            throw new posBuddyIdNotAllocatedException("posBuddy ID not found");
        }
        return identityMapper.toResponse(identityEntity);
    }

    @Override
    public void serveItems(ServingRequest servingRequest, String posBuddyId)
            throws posBuddyIdNotAllocatedException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        AtomicReference<Float> actBalance = new AtomicReference<>(identityEntity.getBalance());
        servingRequest.getServeItems().forEach(serveItem -> {
            try {
                ItemEntity itemEntity = itemRepository.findItemById(serveItem.getItemId());
                RevenueEntity revenueEntity = new RevenueEntity();
                revenueEntity.setAmount(serveItem.getCount());
                revenueEntity.setTimeofaction(LocalDateTime.now());
                revenueEntity.setValue(Double.valueOf(serveItem.getCount() * itemEntity.getPrice()).floatValue());
                revenueEntity.setPosbuddyid(posBuddyId);
                revenueEntity.setItemtext(itemEntity.getItemText());
                revenueEntity.setPaymentaction(Constants.REVENUE);
                revenueRepository.addRevenue(revenueEntity);
                actBalance.set(actBalance.get() - revenueEntity.getValue());
            } catch (ItemNotFoundException e) {
                log.error("Item with id:{} not exists. -> no revenue for this item", serveItem.getItemId());
            }
            identityEntity.setBalance(actBalance.get());
            identityRepository.updateIdentityEntity(identityEntity);
        });

    }


    @Override
    public void addDeposit(String posBuddyId, float value) throws posBuddyIdNotAllocatedException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        float newBalance = identityEntity.getBalance() + value;
        // add revenue
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setAmount(1);
        revenueEntity.setTimeofaction(LocalDateTime.now());
        revenueEntity.setValue(value);
        revenueEntity.setPosbuddyid(posBuddyId);
        revenueEntity.setPaymentaction(Constants.DEPOSIT);
        revenueRepository.addRevenue(revenueEntity);
        // setNewBalance
        identityEntity.setBalance(newBalance);
        identityRepository.updateIdentityEntity(identityEntity);
    }

    /**
     * connect a posBuddyId with a Person
     * this connection exists until you
     * deAllocate
     */
    @Override
    public void allocatePosBuddyId(String posBuddyId, AllocatePosBuddyIdRequest allocatePosBuddyIdRequest)
            throws PosBuddyIdNotAllocateableException, posBuddyIdNotValidException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("PosBuddy ID is not validS");
        }
        boolean isAssignable = identityRepository.isPosBuddyIdAllocatable(posBuddyId);
        if (isAssignable) {
            identityRepository.allocatePosBuddyId(
                    identityMapper.fromRequest(posBuddyId, allocatePosBuddyIdRequest));
            return;
        }
        throw new PosBuddyIdNotAllocateableException("posBuddyId is not assignable");
    }

    @Override
    public void deAllocatePosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("PosBuddy ID is not valid");
        }
        if (identityRepository.isPosBuddyIdAllocatable(posBuddyId)) {
            throw new posBuddyIdNotAllocatedException("posBuddyId is Not allocated");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        identityEntity.setEndallocation(LocalDateTime.now());
        identityRepository.updateIdentityEntity(identityEntity);
    }

    @Override
    @Transactional
    public void payment(String posBuddyId, Float value) throws
            posBuddyIdNotAllocatedException, OutOfBalanceException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity.getBalance() < 0) {
            throw new OutOfBalanceException("Balance is already negativ");
        }
        if (identityEntity.getBalance() - value < 0) {
            throw new OutOfBalanceException("insufficient credit");
        }
        // persist revenue
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setPaymentaction(Constants.PAYMENT);
        revenueEntity.setPosbuddyid(posBuddyId);
        revenueEntity.setAmount(1);
        revenueEntity.setValue(value);
        revenueEntity.setTimeofaction(LocalDateTime.now());
        revenueRepository.addRevenue(revenueEntity);
        // update new balance
        identityEntity.setBalance(identityEntity.getBalance() - value);
        identityRepository.updateIdentityEntity(identityEntity);
    }

    private boolean isNotValidUUID(String posBuddyId) {
        try {
            UUID uuid = UUID.fromString(posBuddyId);
            log.debug("ID:{} is a valid UUID", uuid);
        } catch (Exception exception) {
            log.error("ID: {} is not a valid UUID", posBuddyId);
            return true;
        }
        return false;
    }

}
