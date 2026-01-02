package com.github.diszexuf.activitymanagementbackend.service;

import com.github.diszexuf.activitymanagementbackend.exception.IntervalOverlapException;
import com.github.diszexuf.activitymanagementbackend.exception.InvalidIntervalException;
import com.github.diszexuf.activitymanagementbackend.mapper.IntervalMapper;
import com.github.diszexuf.activitymanagementbackend.model.ActivityType;
import com.github.diszexuf.activitymanagementbackend.model.Interval;
import com.github.diszexuf.activitymanagementbackend.repository.IntervalRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.CreateIntervalRequest;
import org.openapitools.model.IntervalResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class IntervalServiceImpl implements IntervalService {

    IntervalRepository intervalRepository;
    IntervalMapper intervalMapper;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public IntervalResponse createInterval(CreateIntervalRequest request) {
        log.info("Создание интервала: start={}, end={}, type={}", request.getStart(), request.getEnd(), request.getType().name());

        if (request.getStart() >= request.getEnd()) {
            log.warn("Ошибка валидации: start >= end ({} >= {})", request.getStart(), request.getEnd());
            throw new InvalidIntervalException("start должен быть меньше end");
        }

        if (hasOverlaps(request.getStart(), request.getEnd())) {
            log.warn("Обнаружено пересечение для интервала: start={}, end={}", request.getStart(), request.getEnd());
            throw new IntervalOverlapException("Интервал пересекается с существующим");
        }

        Interval interval = new Interval();
        interval.setStart(request.getStart());
        interval.setEnd(request.getEnd());
        interval.setType(ActivityType.valueOf(request.getType().name()));

        Interval savedInterval = intervalRepository.save(interval);

        log.info("Интервал успешно создан: id={}, start={}, end={}, type={}",
                savedInterval.getId(), savedInterval.getStart(),
                savedInterval.getEnd(), savedInterval.getType().toString());

        return intervalMapper.mapToDto(savedInterval);
    }

    @Override
    public List<IntervalResponse> getAllIntervals(Pageable pageable) {
        log.info("Получение всех интервалов");

        List<Interval> intervals = intervalRepository.findAll(pageable).getContent();

        log.info("Найдено {} интервалов", intervals.size());

        return intervalMapper.mapToDtos(intervals);
    }

    private boolean hasOverlaps(Integer start, Integer end) {
        return intervalRepository.existsOverlapping(start, end);
    }

}
