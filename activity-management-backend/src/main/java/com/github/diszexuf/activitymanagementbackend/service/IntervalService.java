package com.github.diszexuf.activitymanagementbackend.service;

import org.openapitools.model.CreateIntervalRequest;
import org.openapitools.model.IntervalResponse;
import org.openapitools.model.IntervalsListResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IntervalService {

    IntervalResponse createInterval(CreateIntervalRequest createIntervalRequest);

    IntervalsListResponse getAllIntervals(Pageable pageable);
}
