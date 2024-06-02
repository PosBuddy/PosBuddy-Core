package de.jkarthaus.posBuddy.mapper;


import de.jkarthaus.posBuddy.model.gui.ReportItemResponse;
import de.jkarthaus.posBuddy.service.impl.ReportServiceImpl;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface ReportItemMapper {

    List<ReportItemResponse> toResponse(
            List<ReportServiceImpl.reportDescriptor> reportDescriptors
    );


}
