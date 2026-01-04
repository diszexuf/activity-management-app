package com.github.diszexuf.activitymanagementbackend.controller;

import com.github.diszexuf.activitymanagementbackend.exception.IntervalOverlapException;
import com.github.diszexuf.activitymanagementbackend.exception.InvalidIntervalException;
import com.github.diszexuf.activitymanagementbackend.service.IntervalService;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IntervalController Unit Tests")
class IntervalControllerTest {

    @Mock
    private IntervalService intervalService;

    @InjectMocks
    private IntervalController intervalController;

    @Nested
    @DisplayName("createInterval - Успешные сценарии")
    class CreateIntervalSuccessTests {

        @Test
        @DisplayName("Должен успешно создать интервал и вернуть статус CREATED")
        void createInterval_WhenValidRequest_ReturnsCreatedStatus() {
            CreateIntervalRequest request = createRequest(1000, 2000, org.openapitools.model.ActivityType.WORK);
            IntervalResponse expectedResponse = createResponse("test-id", 1000, 2000);

            when(intervalService.createInterval(request)).thenReturn(expectedResponse);

            ResponseEntity<IntervalResponse> response = intervalController.createInterval(request);

            assertNotNull(response);
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("test-id", response.getBody().getId());
            assertEquals(1000, response.getBody().getStart());
            assertEquals(2000, response.getBody().getEnd());

            verify(intervalService).createInterval(request);
        }

        @Test
        @DisplayName("Должен создать интервал типа BREAK")
        void createInterval_WithBreakType_ReturnsCreatedStatus() {
            CreateIntervalRequest request = createRequest(3000, 4000, org.openapitools.model.ActivityType.BREAK);
            IntervalResponse expectedResponse = createResponse("break-id", 3000, 4000);
            expectedResponse.setType(org.openapitools.model.ActivityType.BREAK);

            when(intervalService.createInterval(request)).thenReturn(expectedResponse);

            ResponseEntity<IntervalResponse> response = intervalController.createInterval(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(org.openapitools.model.ActivityType.BREAK, response.getBody().getType());

            verify(intervalService).createInterval(request);
        }

        @Test
        @DisplayName("Должен создать интервал на граничных значениях")
        void createInterval_WithBoundaryValues_ReturnsCreatedStatus() {
            CreateIntervalRequest request = createRequest(0, 86400, org.openapitools.model.ActivityType.WORK);
            IntervalResponse expectedResponse = createResponse("boundary-id", 0, 86400);

            when(intervalService.createInterval(request)).thenReturn(expectedResponse);

            ResponseEntity<IntervalResponse> response = intervalController.createInterval(request);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(0, response.getBody().getStart());
            assertEquals(86400, response.getBody().getEnd());

            verify(intervalService).createInterval(request);
        }
    }

    @Nested
    @DisplayName("createInterval - Обработка исключений")
    class CreateIntervalExceptionTests {

        @Test
        @DisplayName("Должен пробросить IntervalOverlapException при пересечении интервалов")
        void createInterval_WhenOverlap_ThrowsIntervalOverlapException() {
            CreateIntervalRequest request = createRequest(1000, 2000, org.openapitools.model.ActivityType.WORK);

            when(intervalService.createInterval(request))
                    .thenThrow(new IntervalOverlapException("Интервал пересекается с существующим"));

            IntervalOverlapException exception = assertThrows(
                    IntervalOverlapException.class,
                    () -> intervalController.createInterval(request)
            );

            assertEquals("Интервал пересекается с существующим", exception.getMessage());
            verify(intervalService).createInterval(request);
        }

        @Test
        @DisplayName("Должен пробросить InvalidIntervalException при невалидных данных")
        void createInterval_WhenInvalidData_ThrowsInvalidIntervalException() {
            CreateIntervalRequest request = createRequest(2000, 1000, org.openapitools.model.ActivityType.WORK);

            when(intervalService.createInterval(request))
                    .thenThrow(new InvalidIntervalException("start должен быть меньше end"));

            InvalidIntervalException exception = assertThrows(
                    InvalidIntervalException.class,
                    () -> intervalController.createInterval(request)
            );

            assertEquals("start должен быть меньше end", exception.getMessage());
            verify(intervalService).createInterval(request);
        }
    }

    @Nested
    @DisplayName("getAllIntervals - Успешные сценарии")
    class GetAllIntervalsSuccessTests {

        @Test
        @DisplayName("Должен вернуть список интервалов с дефолтными параметрами")
        void getAllIntervals_WithDefaultParams_ReturnsOkStatus() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,asc";

            IntervalsListResponse expectedResponse = createListResponse(2);

            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(expectedResponse);

            ResponseEntity<IntervalsListResponse> response = intervalController.getAllIntervals(page, size, sort);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().getIntervals().size());

            verify(intervalService).getAllIntervals(any(Pageable.class));
        }

        @Test
        @DisplayName("Должен вернуть список интервалов с указанной пагинацией")
        void getAllIntervals_WithPagination_ReturnsOkStatus() {
            Integer page = 1;
            Integer size = 5;
            String sort = "start,asc";

            IntervalsListResponse expectedResponse = createListResponse(5);

            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(expectedResponse);

            ResponseEntity<IntervalsListResponse> response = intervalController.getAllIntervals(page, size, sort);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getPageNumber() == 1 &&
                    pageable.getPageSize() == 5 &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен вернуть пустой список когда интервалов нет")
        void getAllIntervals_WhenEmpty_ReturnsEmptyList() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,asc";

            IntervalsListResponse emptyResponse = new IntervalsListResponse();
            emptyResponse.setIntervals(List.of());
            emptyResponse.setTotalElements(0L);

            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(emptyResponse);

            ResponseEntity<IntervalsListResponse> response = intervalController.getAllIntervals(page, size, sort);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(0, response.getBody().getIntervals().size());
            assertEquals(0L, response.getBody().getTotalElements());
        }
    }

    @Nested
    @DisplayName("getAllIntervals - Тестирование сортировки")
    class GetAllIntervalsSortTests {

        @Test
        @DisplayName("Должен использовать дефолтную сортировку когда sort равен null")
        void getAllIntervals_WhenSortIsNull_UsesDefaultSort() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,asc";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен использовать дефолтную сортировку когда sort пустая строка")
        void getAllIntervals_WhenSortIsEmpty_UsesDefaultSort() {
            Integer page = 0;
            Integer size = 10;
            String sort = "";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен использовать дефолтную сортировку когда sort содержит только поле")
        void getAllIntervals_WhenSortHasOnlyField_UsesDefaultSort() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен использовать сортировку по возрастанию когда указан asc")
        void getAllIntervals_WithAscSort_ReturnsAscendingOrder() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,asc";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен использовать сортировку по убыванию когда указан desc")
        void getAllIntervals_WithDescSort_ReturnsDescendingOrder() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,desc";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.DESC
            ));
        }

        @Test
        @DisplayName("Должен корректно обрабатывать сортировку с пробелами")
        void getAllIntervals_WithSpacesInSort_TrimsAndProcesses() {
            Integer page = 0;
            Integer size = 10;
            String sort = "  start  ,  desc  ";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.DESC
            ));
        }

        @Test
        @DisplayName("Должен использовать сортировку по полю end")
        void getAllIntervals_WithEndFieldSort_ReturnsSortedByEnd() {
            Integer page = 0;
            Integer size = 10;
            String sort = "end,asc";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("end") != null &&
                    pageable.getSort().getOrderFor("end").getDirection() == Sort.Direction.ASC
            ));
        }

        @Test
        @DisplayName("Должен использовать дефолтную сортировку при неизвестном направлении")
        void getAllIntervals_WithUnknownDirection_UsesDefaultSort() {
            Integer page = 0;
            Integer size = 10;
            String sort = "start,unknown";

            IntervalsListResponse response = createListResponse(2);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getSort().getOrderFor("start") != null &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.ASC
            ));
        }
    }

    @Nested
    @DisplayName("getAllIntervals - Различные параметры пагинации")
    class GetAllIntervalsPaginationTests {

        @Test
        @DisplayName("Должен корректно обрабатывать первую страницу")
        void getAllIntervals_FirstPage_ReturnsCorrectPage() {
            Integer page = 0;
            Integer size = 20;
            String sort = "start, asc";

            IntervalsListResponse response = createListResponse(20);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getPageNumber() == 0 &&
                    pageable.getPageSize() == 20
            ));
        }

        @Test
        @DisplayName("Должен корректно обрабатывать последующие страницы")
        void getAllIntervals_SubsequentPage_ReturnsCorrectPage() {
            Integer page = 2;
            Integer size = 15;
            String sort = "start,desc";

            IntervalsListResponse response = createListResponse(15);
            when(intervalService.getAllIntervals(any(Pageable.class))).thenReturn(response);

            intervalController.getAllIntervals(page, size, sort);

            verify(intervalService).getAllIntervals(argThat(pageable ->
                    pageable.getPageNumber() == 2 &&
                    pageable.getPageSize() == 15 &&
                    pageable.getSort().getOrderFor("start").getDirection() == Sort.Direction.DESC
            ));
        }
    }

    private CreateIntervalRequest createRequest(Integer start, Integer end, org.openapitools.model.ActivityType type) {
        CreateIntervalRequest request = new CreateIntervalRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setType(type);
        return request;
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

    private IntervalsListResponse createListResponse(int count) {
        IntervalsListResponse response = new IntervalsListResponse();
        List<IntervalResponse> intervals = Arrays.asList(
                createResponse("id1", 1000, 2000),
                createResponse("id2", 3000, 4000)
        );
        response.setIntervals(intervals.subList(0, Math.min(count, intervals.size())));
        response.setTotalElements((long) count);
        return response;
    }
}

