package com.github.diszexuf.activitymanagementbackend.controller;

import com.github.diszexuf.activitymanagementbackend.service.IntervalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.api.IntervalsApi;
import org.openapitools.model.CreateIntervalRequest;
import org.openapitools.model.IntervalResponse;
import org.openapitools.model.IntervalsListResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class IntervalController implements IntervalsApi {

    private final IntervalService intervalService;

    @Override
    public ResponseEntity<IntervalResponse> createInterval(CreateIntervalRequest createIntervalRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(intervalService.createInterval(createIntervalRequest));
    }

    @Override
    public ResponseEntity<IntervalsListResponse> getAllIntervals(Integer page, Integer size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return ResponseEntity.ok(intervalService.getAllIntervals(pageable));
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        Sort.Direction direction = "desc".equals(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;

        return Sort.by(direction, parts[0]);
    }

}
