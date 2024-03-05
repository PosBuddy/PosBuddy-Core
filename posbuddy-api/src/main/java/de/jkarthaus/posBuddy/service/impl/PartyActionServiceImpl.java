package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.IdentityRepository;
import de.jkarthaus.posBuddy.db.ItemRepository;
import de.jkarthaus.posBuddy.db.RevenueRepository;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.mapper.RevenueMapper;
import de.jkarthaus.posBuddy.model.Constants;
import de.jkarthaus.posBuddy.model.gui.AllocatePosBuddyIdRequest;
import de.jkarthaus.posBuddy.model.gui.IdentityResponse;
import de.jkarthaus.posBuddy.model.gui.RevenueResponse;
import de.jkarthaus.posBuddy.model.gui.ServeItem;
import de.jkarthaus.posBuddy.service.PartyActionService;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class PartyActionServiceImpl implements PartyActionService {

    private final IdentityMapper identityMapper;
    private final RevenueMapper revenueMapper;
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
    public List<RevenueResponse> getRevenueResponseByPosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("no valid UUID");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity == null) {
            log.info("Actual valid posBuddy Identity with ID:{} not found in Database", posBuddyId);
            throw new posBuddyIdNotAllocatedException("posBuddy ID not found");
        }
        log.info("get revenue for {}{}", identityEntity.getSurname(), identityEntity.getLastname());
        return revenueMapper.toResponse(
                revenueRepository.getRevenuesByIdSince(
                        posBuddyId,
                        identityEntity.getStartallocation()
                )
        );
    }

    @Override
    public void serveItems(List<ServeItem> serveItems, String posBuddyId)
            throws posBuddyIdNotAllocatedException, OutOfBalanceException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        AtomicReference<Float> actBalance = new AtomicReference<>(identityEntity.getBalance());
        record serveItemRecord(String itemText, double price) {
        }
        HashMap<String, serveItemRecord> priceMap = new HashMap<>();
        AtomicReference<Double> summ = new AtomicReference<>(0.0);
        serveItems.forEach(serveItem -> {
            if (!priceMap.containsKey(serveItem.getItemId())) {
                ItemEntity itemEntity = new ItemEntity();
                try {
                    itemEntity = itemRepository.findItemById(serveItem.getItemId());
                } catch (ItemNotFoundException e) {
                    log.error("No Item:{} found", serveItem.getItemId());
                    itemEntity.setPrice(0.0);
                    itemEntity.setItemText("UNKNOWN");
                }
                priceMap.put(
                        serveItem.getItemId(),
                        new serveItemRecord(itemEntity.getItemText(), itemEntity.getPrice())
                );
            }
            summ.updateAndGet(v -> v + (serveItem.getCount() * priceMap.get(serveItem.getItemId()).price));
        });
        if (summ.get().compareTo(actBalance.get().doubleValue()) > 0) {
            log.error("Summ of order is grater than actual balance");
            throw new OutOfBalanceException("Summ of order is grater than actual balance");
        }
        // add revenue Entry
        serveItems.forEach(serveItem -> {
                    RevenueEntity revenueEntity = new RevenueEntity();
                    revenueEntity.setAmount(serveItem.getCount());
                    revenueEntity.setTimeofaction(LocalDateTime.now());
                    revenueEntity.setValue(Double.valueOf(priceMap.get(serveItem.getItemId()).price).floatValue());
                    revenueEntity.setPosbuddyid(posBuddyId);
                    revenueEntity.setItemtext(priceMap.get(serveItem.getItemId()).itemText);
                    revenueEntity.setPaymentaction(Constants.REVENUE);
                    revenueRepository.addRevenue(revenueEntity);
                }
        );
        // update balance
        identityEntity.setBalance(actBalance.get() - summ.get().floatValue());
        identityRepository.updateIdentityEntity(identityEntity);
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
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException, OutOfBalanceException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("PosBuddy ID is not valid");
        }
        if (identityRepository.isPosBuddyIdAllocatable(posBuddyId)) {
            throw new posBuddyIdNotAllocatedException("posBuddyId is Not allocated");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity.getBalance() > 0) {
            throw new OutOfBalanceException("balance is > 0");
        }
        identityEntity.setEndallocation(LocalDateTime.now());
        identityRepository.updateIdentityEntity(identityEntity);
    }

    @Override
    @Transactional
    public void payout(String posBuddyId) throws posBuddyIdNotAllocatedException, OutOfBalanceException {
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity.getBalance() <= 0) {
            throw new OutOfBalanceException("Balance is already negativ");
        }
        // persist revenue
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setPaymentaction(Constants.PAYMENT);
        revenueEntity.setPosbuddyid(posBuddyId);
        revenueEntity.setAmount(1);
        revenueEntity.setValue(identityEntity.getBalance());
        revenueEntity.setTimeofaction(LocalDateTime.now());
        revenueRepository.addRevenue(revenueEntity);
        // update new balance
        identityEntity.setBalance(0f);
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
