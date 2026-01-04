package com.github.diszexuf.activitymanagementbackend.service;

import com.github.diszexuf.activitymanagementbackend.exception.IntervalOverlapException;
import com.github.diszexuf.activitymanagementbackend.exception.InvalidIntervalException;
import com.github.diszexuf.activitymanagementbackend.mapper.IntervalMapper;
import com.github.diszexuf.activitymanagementbackend.model.ActivityType;
import com.github.diszexuf.activitymanagementbackend.model.Interval;
import com.github.diszexuf.activitymanagementbackend.repository.IntervalRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.CreateIntervalRequest;
import org.openapitools.model.IntervalResponse;
import org.openapitools.model.IntervalsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntervalService Unit Tests")
class IntervalServiceImplTest {

    @Mock
    private IntervalRepository intervalRepository;

    @Mock
    private IntervalMapper intervalMapper;

    @InjectMocks
    private IntervalServiceImpl intervalService;

    @Nested
    @DisplayName("createInterval - Успешные сценарии")
    class CreateIntervalSuccessTests {

        @Test
        @DisplayName("Должен успешно создать интервал без пересечений")
        void createInterval_WhenNoOverlap_Success() {
            CreateIntervalRequest request = createRequest(1000, 2000, org.openapitools.model.ActivityType.WORK);

            Interval savedInterval = createInterval(UUID.randomUUID(), 1000, 2000, ActivityType.WORK);
            IntervalResponse expectedResponse = createResponse("test-id", 1000, 2000);

            when(intervalRepository.existsOverlapping(1000, 2000)).thenReturn(false);
            when(intervalRepository.save(any(Interval.class))).thenReturn(savedInterval);
            when(intervalMapper.mapToDto(savedInterval)).thenReturn(expectedResponse);

            IntervalResponse result = intervalService.createInterval(request);

            assertNotNull(result);
            assertEquals("test-id", result.getId());
            assertEquals(1000, result.getStart());
            assertEquals(2000, result.getEnd());

            verify(intervalRepository).existsOverlapping(1000, 2000);
            verify(intervalRepository).save(any(Interval.class));
            verify(intervalMapper).mapToDto(savedInterval);
        }

        @Test
        @DisplayName("Должен создать интервал типа WORK")
        void createInterval_WorkType_Success() {
            CreateIntervalRequest request = createRequest(0, 1000, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(anyInt(), anyInt())).thenReturn(false);
            when(intervalRepository.save(any())).thenReturn(createInterval(UUID.randomUUID(), 0, 1000, ActivityType.WORK));
            when(intervalMapper.mapToDto(any())).thenReturn(createResponse("id", 0, 1000));

            IntervalResponse result = intervalService.createInterval(request);

            assertNotNull(result);
            verify(intervalRepository).save(any(Interval.class));
        }

        @Test
        @DisplayName("Должен создать интервал типа BREAK")
        void createInterval_BreakType_Success() {
            CreateIntervalRequest request = createRequest(3000, 4000, org.openapitools.model.ActivityType.BREAK);

            when(intervalRepository.existsOverlapping(anyInt(), anyInt())).thenReturn(false);
            when(intervalRepository.save(any())).thenReturn(createInterval(UUID.randomUUID(), 3000, 4000, ActivityType.BREAK));
            when(intervalMapper.mapToDto(any())).thenReturn(createResponse("id", 3000, 4000));

            IntervalResponse result = intervalService.createInterval(request);

            assertNotNull(result);
            verify(intervalRepository).save(any(Interval.class));
        }

        @Test
        @DisplayName("Должен создать интервал на границах диапазона (0-86400)")
        void createInterval_BoundaryValues_Success() {
            CreateIntervalRequest request = createRequest(0, 86400, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(0, 86400)).thenReturn(false);
            when(intervalRepository.save(any())).thenReturn(createInterval(UUID.randomUUID(), 0, 86400, ActivityType.WORK));
            when(intervalMapper.mapToDto(any())).thenReturn(createResponse("id", 0, 86400));

            IntervalResponse result = intervalService.createInterval(request);

            assertNotNull(result);
            verify(intervalRepository).existsOverlapping(0, 86400);
        }
    }

    @Nested
    @DisplayName("createInterval - Валидация")
    class CreateIntervalValidationTests {

        @Test
        @DisplayName("Должен выбросить исключение когда start равен end")
        void createInterval_WhenStartEqualsEnd_ThrowsException() {
            CreateIntervalRequest request = createRequest(1000, 1000, org.openapitools.model.ActivityType.WORK);

            InvalidIntervalException exception = assertThrows(
                    InvalidIntervalException.class,
                    () -> intervalService.createInterval(request)
            );

            assertEquals("start должен быть меньше end", exception.getMessage());
            verify(intervalRepository, never()).save(any());
            verify(intervalRepository, never()).existsOverlapping(anyInt(), anyInt());
        }

        @Test
        @DisplayName("Должен выбросить исключение когда start больше end")
        void createInterval_WhenStartGreaterThanEnd_ThrowsException() {
            CreateIntervalRequest request = createRequest(2000, 1000, org.openapitools.model.ActivityType.WORK);

            InvalidIntervalException exception = assertThrows(
                    InvalidIntervalException.class,
                    () -> intervalService.createInterval(request)
            );

            assertEquals("start должен быть меньше end", exception.getMessage());
            verify(intervalRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("createInterval - Пересечения интервалов")
    class CreateIntervalOverlapTests {

        @Test
        @DisplayName("Должен выбросить исключение при полном совпадении интервалов")
        void createInterval_ExactOverlap_ThrowsException() {
            CreateIntervalRequest request = createRequest(1000, 2000, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(1000, 2000)).thenReturn(true);

            IntervalOverlapException exception = assertThrows(
                    IntervalOverlapException.class,
                    () -> intervalService.createInterval(request)
            );

            assertEquals("Интервал пересекается с существующим", exception.getMessage());
            verify(intervalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить исключение когда новый интервал внутри существующего")
        void createInterval_NewInsideExisting_ThrowsException() {
            CreateIntervalRequest request = createRequest(1500, 2500, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(1500, 2500)).thenReturn(true);

            assertThrows(IntervalOverlapException.class, () -> {
                intervalService.createInterval(request);
            });

            verify(intervalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить исключение когда новый интервал охватывает существующий")
        void createInterval_NewCoversExisting_ThrowsException() {
            CreateIntervalRequest request = createRequest(500, 3000, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(500, 3000)).thenReturn(true);

            assertThrows(IntervalOverlapException.class, () -> {
                intervalService.createInterval(request);
            });

            verify(intervalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить исключение при пересечении слева")
        void createInterval_OverlapLeft_ThrowsException() {
            CreateIntervalRequest request = createRequest(1000, 2500, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(1000, 2500)).thenReturn(true);

            assertThrows(IntervalOverlapException.class, () -> {
                intervalService.createInterval(request);
            });

            verify(intervalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен выбросить исключение при пересечении справа")
        void createInterval_OverlapRight_ThrowsException() {
            CreateIntervalRequest request = createRequest(1500, 3000, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(1500, 3000)).thenReturn(true);

            assertThrows(IntervalOverlapException.class, () -> {
                intervalService.createInterval(request);
            });

            verify(intervalRepository, never()).save(any());
        }

        @Test
        @DisplayName("Должен успешно создать интервал когда границы касаются (без пересечения)")
        void createInterval_TouchingBoundaries_Success() {
            CreateIntervalRequest request = createRequest(2000, 3000, org.openapitools.model.ActivityType.WORK);

            when(intervalRepository.existsOverlapping(2000, 3000)).thenReturn(false);
            when(intervalRepository.save(any())).thenReturn(createInterval(UUID.randomUUID(), 2000, 3000, ActivityType.WORK));
            when(intervalMapper.mapToDto(any())).thenReturn(createResponse("id", 2000, 3000));

            IntervalResponse result = intervalService.createInterval(request);

            assertNotNull(result);
            verify(intervalRepository).save(any());
        }
    }

    @Nested
    @DisplayName("getAllIntervals - Получение списка")
    class GetAllIntervalsTests {

        @Test
        @DisplayName("Должен вернуть список всех интервалов")
        void getAllIntervals_ReturnsAllIntervals() {
            Pageable pageable = PageRequest.of(0, 10);

            List<Interval> intervals = Arrays.asList(
                    createInterval(UUID.randomUUID(), 1000, 2000, ActivityType.WORK),
                    createInterval(UUID.randomUUID(), 3000, 4000, ActivityType.BREAK)
            );

            Page<Interval> page = new PageImpl<>(intervals);

            List<IntervalResponse> responses = Arrays.asList(
                    createResponse("id1", 1000, 2000),
                    createResponse("id2", 3000, 4000)
            );

            when(intervalRepository.findAll(pageable)).thenReturn(page);
            when(intervalRepository.count()).thenReturn(2L);
            when(intervalMapper.mapToDtos(intervals)).thenReturn(responses);

            IntervalsListResponse result = intervalService.getAllIntervals(pageable);

            assertNotNull(result);
            assertEquals(2, result.getIntervals().size());
            assertEquals(2L, result.getTotalElements());

            verify(intervalRepository).findAll(pageable);
            verify(intervalRepository).count();
            verify(intervalMapper).mapToDtos(intervals);
        }

        @Test
        @DisplayName("Должен вернуть пустой список когда интервалов нет")
        void getAllIntervals_WhenEmpty_ReturnsEmptyList() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Interval> emptyPage = new PageImpl<>(List.of());

            when(intervalRepository.findAll(pageable)).thenReturn(emptyPage);
            when(intervalRepository.count()).thenReturn(0L);
            when(intervalMapper.mapToDtos(List.of())).thenReturn(List.of());

            IntervalsListResponse result = intervalService.getAllIntervals(pageable);

            assertNotNull(result);
            assertEquals(0, result.getIntervals().size());
            assertEquals(0L, result.getTotalElements());
        }

        @Test
        @DisplayName("Должен корректно работать с пагинацией")
        void getAllIntervals_WithPagination_ReturnsCorrectPage() {
            Pageable pageable = PageRequest.of(1, 5);

            List<Interval> intervals = List.of(
                    createInterval(UUID.randomUUID(), 5000, 6000, ActivityType.WORK)
            );

            Page<Interval> page = new PageImpl<>(intervals, pageable, 10);

            when(intervalRepository.findAll(pageable)).thenReturn(page);
            when(intervalRepository.count()).thenReturn(10L);
            when(intervalMapper.mapToDtos(intervals)).thenReturn(
                    List.of(createResponse("id", 5000, 6000))
            );

            IntervalsListResponse result = intervalService.getAllIntervals(pageable);

            assertNotNull(result);
            assertEquals(1, result.getIntervals().size());
            assertEquals(10L, result.getTotalElements());

            verify(intervalRepository).findAll(pageable);
        }
    }

    private CreateIntervalRequest createRequest(Integer start, Integer end, org.openapitools.model.ActivityType type) {
        CreateIntervalRequest request = new CreateIntervalRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setType(type);
        return request;
    }

    private Interval createInterval(UUID id, Integer start, Integer end, ActivityType type) {
        Interval interval = new Interval();
        interval.setId(id);
        interval.setStart(start);
        interval.setEnd(end);
        interval.setType(type);
        interval.setCreatedAt(OffsetDateTime.now());
        return interval;
    }

    private IntervalResponse createResponse(String id, Integer start, Integer end) {
        IntervalResponse response = new IntervalResponse();
        response.setId(id);
        response.setStart(start);
        response.setEnd(end);
        response.setType(org.openapitools.model.ActivityType.WORK);
        response.setCreatedAt(OffsetDateTime.now());
        return response;
    }
}