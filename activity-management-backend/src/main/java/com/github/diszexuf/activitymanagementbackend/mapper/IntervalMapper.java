package com.github.diszexuf.activitymanagementbackend.mapper;

import com.github.diszexuf.activitymanagementbackend.model.Interval;
import org.openapitools.model.ActivityType;
import org.openapitools.model.IntervalResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IntervalMapper {

    public IntervalResponse mapToDto(Interval entity) {
        IntervalResponse dto = new IntervalResponse();

        dto.setId(entity.getId().toString());
        dto.setStart(entity.getStart());
        dto.setEnd(entity.getEnd());
        dto.setType(ActivityType.valueOf(entity.getType().name()));
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }

    public List<IntervalResponse> mapToDtos(List<Interval> entities) {
        return entities.stream().map(this::mapToDto).toList();
    }
}
