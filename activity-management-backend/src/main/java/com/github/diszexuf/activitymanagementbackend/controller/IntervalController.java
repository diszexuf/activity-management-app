package com.github.diszexuf.activitymanagementbackend.controller;

import com.github.diszexuf.activitymanagementbackend.service.IntervalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.openapitools.api.IntervalsApi;
import org.openapitools.model.CreateIntervalRequest;
import org.openapitools.model.IntervalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IntervalController implements IntervalsApi {

    private final IntervalService intervalService;

    @Override
    public ResponseEntity<IntervalResponse> createInterval(@RequestBody @Valid CreateIntervalRequest createIntervalRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(intervalService.createInterval(createIntervalRequest));
    }

    @Override
    public ResponseEntity<List<IntervalResponse>> getAllIntervals() {
        return ResponseEntity.ok(intervalService.getAllIntervals());
    }
}
