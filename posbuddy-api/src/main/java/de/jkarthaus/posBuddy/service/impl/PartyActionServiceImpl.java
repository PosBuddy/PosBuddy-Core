package de.jkarthaus.posBuddy.service.impl;

import de.jkarthaus.posBuddy.db.*;
import de.jkarthaus.posBuddy.db.entities.ConfigEntity;
import de.jkarthaus.posBuddy.db.entities.IdentityEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.db.entities.RevenueEntity;
import de.jkarthaus.posBuddy.exception.*;
import de.jkarthaus.posBuddy.mapper.DispensingStationMapper;
import de.jkarthaus.posBuddy.mapper.IdentityMapper;
import de.jkarthaus.posBuddy.mapper.RevenueMapper;
import de.jkarthaus.posBuddy.model.Constants;
import de.jkarthaus.posBuddy.model.config.DepositBonusConfig;
import de.jkarthaus.posBuddy.model.enums.ConfigID;
import de.jkarthaus.posBuddy.model.gui.*;
import de.jkarthaus.posBuddy.service.PartyActionService;
import de.jkarthaus.posBuddy.tools.Tools;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Singleton
public class PartyActionServiceImpl implements PartyActionService {

    private final ObjectMapper objectMapper;
    private final IdentityMapper identityMapper;
    private final RevenueMapper revenueMapper;
    private final DispensingStationMapper dispensingStationMapper;
    private final IdentityRepository identityRepository;
    private final RevenueRepository revenueRepository;
    private final ItemRepository itemRepository;
    private final ConfigRepository configRepository;

    private final DispensingStationRepository dispensingStationRepository;

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
    @Transactional
    public void addDeposit(String posBuddyId, Float value) throws posBuddyIdNotAllocatedException, IOException {
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
        // check discounts
        Optional<ConfigEntity> configEntityOptional = configRepository.findById(ConfigID.DEP_BONUS_CONFIG);
        if (configEntityOptional.isPresent()) {
            log.debug("check discount");
            DepositBonusConfig depositBonusConfig = objectMapper.readValue(
                    configEntityOptional.get().getJsonConfig(),
                    DepositBonusConfig.class
            );
            List<RevenueEntity> depositBonusRevenueEntities = calculateDepositBonus(
                    depositBonusConfig,
                    value.doubleValue(),
                    identityEntity
            );
            AtomicReference<Double> depositBonus = new AtomicReference<>(0.0);
            depositBonusRevenueEntities.forEach(depositBonusRevenueEntity -> {
                revenueRepository.addRevenue(depositBonusRevenueEntity);
                depositBonus.updateAndGet(v -> v + depositBonusRevenueEntity.getValue().doubleValue());
            });
            log.info("add:{} bonus value to new balance", depositBonus);
            newBalance += depositBonus.get();
        } else {
            log.debug("no discount config found");
        }
        // setNewBalance
        identityEntity.setBalance(newBalance);
        identityRepository.updateIdentityEntity(identityEntity);
    }

    private List<RevenueEntity> calculateDepositBonus(
            DepositBonusConfig depositBonusConfig,
            double depositValue,
            IdentityEntity identityEntity) {
        List<RevenueEntity> revenueEntities = new ArrayList<>();
        depositBonusConfig.depositBonusList.forEach(depositBonus -> {
                    // -- bonus for static identity
                    if (identityEntity.isStaticIdentity() && depositBonus.isActiveOnStaticId()) {
                        if (depositValue >= depositBonus.getFromAmount()
                                && (depositValue <= depositBonus.getToAmount() || depositBonus.getToAmount() <= 0)
                        ) {
                            log.info("generating for static id bonus :{}", depositBonus.getRevenueText());
                            revenueEntities.add(getBonusRevenueEntity(
                                            identityEntity.getPosbuddyid(),
                                            depositBonus.getRevenueText(),
                                            depositValue / 100 * depositBonus.getCreditNotePercent()
                                    )
                            );
                        }
                    }
                    // -- bonus for volatile identity
                    if (!identityEntity.isStaticIdentity() && !depositBonus.isActiveOnStaticId()) {
                        if (depositValue >= depositBonus.getFromAmount()
                                && (depositValue <= depositBonus.getToAmount() || depositBonus.getToAmount() <= 0)
                        ) {
                            log.info("generating bonus for volatile id :{}", depositBonus.getRevenueText());
                            revenueEntities.add(getBonusRevenueEntity(
                                            identityEntity.getPosbuddyid(),
                                            depositBonus.getRevenueText(),
                                            depositValue / 100 * depositBonus.getCreditNotePercent()
                                    )
                            );
                        }
                    }
                }
        );
        return revenueEntities;
    }

    private RevenueEntity getBonusRevenueEntity(
            String posBuddyId,
            String text,
            Double value
    ) {
        RevenueEntity revenueEntity = new RevenueEntity();
        revenueEntity.setPosbuddyid(posBuddyId);
        revenueEntity.setPaymentaction(Constants.DEPOSIT);
        revenueEntity.setItemtext(text);
        revenueEntity.setAmount(1);
        revenueEntity.setValue(value.floatValue());
        revenueEntity.setTimeofaction(LocalDateTime.now());
        return revenueEntity;
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
            throw new posBuddyIdNotValidException("PosBuddy ID is not valid");
        }
        boolean isAssignable = identityRepository.isPosBuddyIdAllocatable(posBuddyId);
        if (isAssignable) {
            log.info("assign new posBuddyId:{} to {}",
                    posBuddyId,
                    allocatePosBuddyIdRequest.getSurname() + " " + allocatePosBuddyIdRequest.getLastname()
            );
            identityRepository.allocatePosBuddyId(
                    identityMapper.fromRequest(posBuddyId, allocatePosBuddyIdRequest)
            );
            if (allocatePosBuddyIdRequest.getBalance() > 0) {
                log.info("add deposit start revenue:{}",
                        allocatePosBuddyIdRequest.getBalance()
                );
                RevenueEntity revenueEntity = new RevenueEntity();
                revenueEntity.setAmount(1);
                revenueEntity.setItemtext("Startguthaben");
                revenueEntity.setTimeofaction(LocalDateTime.now());
                revenueEntity.setValue(allocatePosBuddyIdRequest.getBalance());
                revenueEntity.setPosbuddyid(posBuddyId);
                revenueEntity.setPaymentaction(Constants.DEPOSIT);
                revenueRepository.addRevenue(revenueEntity);
            }
            return;
        }
        log.warn("id:{} is not assignable", posBuddyId);
        throw new PosBuddyIdNotAllocateableException("posBuddyId is not assignable");
    }

    @Override
    public List<DispensingStationResponse> getDispensingStations() {
        return dispensingStationMapper.toResponse(
                dispensingStationRepository.getDispensingStations()
        );
    }


    @Override
    public void deAllocatePosBuddyId(String posBuddyId)
            throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException, OutOfBalanceException, PosBuddyIdStaticException {
        if (isNotValidUUID(posBuddyId)) {
            log.warn("posBuddy ID:{} is not valid", posBuddyId);
            throw new posBuddyIdNotValidException("posBuddy ID is not valid");
        }
        if (identityRepository.isPosBuddyIdAllocatable(posBuddyId)) {
            log.error("posbuddy ID:{} is not allocated", posBuddyId);
            throw new posBuddyIdNotAllocatedException("posBuddyId is Not allocated");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity.isStaticIdentity()) {
            throw new PosBuddyIdStaticException("posBuddy ID is static");
        }
        if (identityEntity.getBalance() > 0) {
            throw new OutOfBalanceException("balance is > 0");
        }
        identityEntity.setEndallocation(LocalDateTime.now());
        identityRepository.updateIdentityEntity(identityEntity);
        log.info("deAllocation of ID:{} successfully", posBuddyId);
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

    @Override
    public IdDataResponse getIdDataResponse(String posBuddyId) throws posBuddyIdNotValidException, posBuddyIdNotAllocatedException {
        if (isNotValidUUID(posBuddyId)) {
            throw new posBuddyIdNotValidException("no valid UUID");
        }
        IdentityEntity identityEntity = identityRepository.findById(posBuddyId);
        if (identityEntity == null) {
            log.info("Actual valid posBuddy Identity with ID:{} not found in Database", posBuddyId);
            throw new posBuddyIdNotAllocatedException("posBuddy ID not found");
        }
        List<IdDataResponse.Revenue> revenueList = revenueRepository
                .getRevenuesByIdDescending(posBuddyId, identityEntity.getStartallocation())
                .stream()
                .map(revenueEntity -> new IdDataResponse.Revenue(
                                revenueEntity.getItemtext(),
                                revenueEntity.getAmount(),
                                revenueEntity.getValue(),
                                revenueEntity.getPaymentaction(),
                                revenueEntity.getTimeofaction()
                        )
                ).collect(Collectors.toList());
        return new IdDataResponse(
                posBuddyId,
                identityEntity.getSurname(),
                identityEntity.getLastname(),
                Tools.isAgeUnderYouthProtection(
                        identityEntity.getBirthday()
                ),
                identityEntity.getBalance(),
                revenueList
        );
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
