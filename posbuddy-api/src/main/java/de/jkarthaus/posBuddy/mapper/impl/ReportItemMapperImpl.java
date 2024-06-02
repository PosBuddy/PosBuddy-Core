package de.jkarthaus.posBuddy.mapper.impl;
import de.jkarthaus.posBuddy.mapper.ReportItemMapper;
import de.jkarthaus.posBuddy.model.gui.ReportItemResponse;
import de.jkarthaus.posBuddy.service.impl.ReportServiceImpl;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public class ReportItemMapperImpl implements ReportItemMapper {
    @Override
    public List<ReportItemResponse> toResponse(
            List<ReportServiceImpl.reportDescriptor> reportDescriptors) {
        return reportDescriptors
                .stream()
                .map(reportDescriptor -> new ReportItemResponse(
                                reportDescriptor.reportType(),
                                reportDescriptor.fileName(),
                                reportDescriptor.creationDate()
                        )
                ).toList();
    }
}
