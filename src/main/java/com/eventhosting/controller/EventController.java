package com.eventhosting.controller;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.DeletedEventResponse;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.dto.response.ResponseBean;
import com.eventhosting.service.HostingEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final HostingEventService hostingEventService;

    public EventController(HostingEventService hostingEventService) {
        this.hostingEventService = hostingEventService;
    }

    @PostMapping
    public ResponseEntity<ResponseBean<EventResponse>> submitEvent(
            @Valid @RequestBody EventCreateRequest request) {
        EventResponse response = hostingEventService.submitEvent(request);
        return new ResponseEntity<>(
                ResponseBean.success("Event submitted successfully for admin review", response),
                HttpStatus.CREATED);
    }

    /**
     * Public marketplace: approved events only.
     */
    @GetMapping("/hosted")
    public ResponseEntity<ResponseBean<List<EventResponse>>> getHostedEvents() {
        List<EventResponse> events = hostingEventService.getHostedEvents();
        return ResponseEntity.ok(
                ResponseBean.success("Hosted events retrieved successfully", events));
    }

    /**
     * Every event, newest first; narrow it with ?status=PENDING|APPROVED|REJECTED.
     */
    @GetMapping
    public ResponseEntity<ResponseBean<List<EventResponse>>> getAllEvents(
            @RequestParam(required = false) String status) {
        List<EventResponse> events = hostingEventService.getAllEvents(status);
        return ResponseEntity.ok(
                ResponseBean.success("Events retrieved successfully", events));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBean<EventResponse>> getEventById(@PathVariable Long id) {
        EventResponse event = hostingEventService.getEventById(id);
        return ResponseEntity.ok(
                ResponseBean.success("Event retrieved successfully", event));
    }

    /**
     * Removes the movie and every review attached to it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBean<DeletedEventResponse>> deleteEvent(@PathVariable Long id) {
        DeletedEventResponse deleted = hostingEventService.deleteEvent(id);
        return ResponseEntity.ok(
                ResponseBean.success("Movie deleted successfully", deleted));
    }
}
