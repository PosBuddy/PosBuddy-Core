package de.jkarthaus.posBuddy.mapper.impl;

import de.jkarthaus.posBuddy.mapper.PermissionMapper;
import de.jkarthaus.posBuddy.model.gui.PermissionResponse;
import de.jkarthaus.posBuddy.service.SecurityService;
import jakarta.inject.Singleton;

@Singleton
public class PermissionMapperImpl implements PermissionMapper {
    @Override
    public PermissionResponse toResponse(SecurityService.permissionRecord permissionRecord) {
        return new PermissionResponse(
                permissionRecord.servePermission(),
                permissionRecord.checkoutPermission()
        );
    }
}
