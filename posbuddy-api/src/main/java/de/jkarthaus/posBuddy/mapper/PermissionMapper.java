package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.db.entities.DispensingStationEntity;
import de.jkarthaus.posBuddy.db.entities.ItemEntity;
import de.jkarthaus.posBuddy.model.gui.ItemResponse;
import de.jkarthaus.posBuddy.model.gui.PermissionResponse;
import de.jkarthaus.posBuddy.service.SecurityService;
import de.jkarthaus.posBuddy.service.impl.SecurityServiceImpl;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface PermissionMapper {



    PermissionResponse toResponse(SecurityService.permissionRecord permissionRecord);
}
