package com.eventhosting.service;

import com.eventhosting.dto.request.EventCreateRequest;
import com.eventhosting.dto.response.EventResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.Organizer;
import com.eventhosting.exception.BadRequestException;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.dto.response.DeletedEventResponse;
import com.eventhosting.repository.HostingEventRepository;
import com.eventhosting.repository.MovieReviewRepository;
import com.eventhosting.repository.OrganizerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HostingEventService {

    private static final Logger logger = LoggerFactory.getLogger(HostingEventService.class);

    private final HostingEventRepository hostingEventRepository;
    private final OrganizerRepository organizerRepository;
    private final MovieReviewRepository movieReviewRepository;

    public HostingEventService(HostingEventRepository hostingEventRepository,
                               OrganizerRepository organizerRepository,
                               MovieReviewRepository movieReviewRepository) {
        this.hostingEventRepository = hostingEventRepository;
        this.organizerRepository = organizerRepository;
        this.movieReviewRepository = movieReviewRepository;
    }

    /**
     * Stores the proposal as PENDING along with its 1:1 moderation record, so
     * the audit trail exists from the moment the event does.
     */
    @Transactional
    public EventResponse submitEvent(EventCreateRequest request) {
        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organizer not found with id " + request.getOrganizerId()));

        HostingEvent event = new HostingEvent();
        event.setOrganizer(organizer);
        event.setEventName(request.getEventName().trim());
        event.setEventType(request.getEventType().trim());
        event.setEventGenre(request.getEventGenre().trim());
        event.setDescription(request.getDescription());
        event.setBaseTicketPrice(request.getBaseTicketPrice());
        event.setAttendeeName(request.getAttendeeName().trim());
        event.setAttendeeRole(request.getAttendeeRole().trim());
        event.setVenue(request.getVenue());
        event.setEventDate(request.getEventDate());
        event.setApprovalStatus(ApprovalStatus.PENDING);
        event.setApproval(new Approval(event, organizer));

        HostingEvent saved = hostingEventRepository.save(event);
        logger.info("Event submitted: id={} name='{}' organizerId={}",
                saved.getEventId(), saved.getEventName(), organizer.getOrganizerId());

        return EventResponse.from(saved);
    }

    /**
     * Public marketplace listing: approved events only.
     */
    @Transactional(readOnly = true)
    public List<EventResponse> getHostedEvents() {
        return hostingEventRepository.findByStatusWithOrganizer(ApprovalStatus.APPROVED)
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    /**
     * Admin listing. A blank status returns every submission.
     */
    @Transactional(readOnly = true)
    public List<EventResponse> getAllEvents(String status) {
        List<HostingEvent> events;
        if (status == null || status.isBlank() || "all".equalsIgnoreCase(status)) {
            events = hostingEventRepository.findAllWithOrganizer();
        } else {
            ApprovalStatus parsed;
            try {
                parsed = ApprovalStatus.from(status);
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException(
                        "Unknown status '" + status + "'. Use PENDING, APPROVED, REJECTED or ALL.");
            }
            events = hostingEventRepository.findByStatusWithOrganizer(parsed);
        }
        return events.stream().map(EventResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {
        return hostingEventRepository.findByIdWithOrganizer(id)
                .map(EventResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));
    }

    /**
     * Removes a movie. Its reviews go first because they hold a non-null
     * foreign key to the event; the approval record is removed by the cascade
     * on HostingEvent.
     */
    @Transactional
    public DeletedEventResponse deleteEvent(Long id) {
        HostingEvent event = hostingEventRepository.findByIdWithOrganizer(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + id));

        String name = event.getEventName();
        long reviewsDeleted = movieReviewRepository.deleteByHostingEvent_EventId(id);
        hostingEventRepository.delete(event);

        logger.info("Event deleted: id={} name='{}' reviewsDeleted={}", id, name, reviewsDeleted);
        return new DeletedEventResponse(id, name, reviewsDeleted);
    }
}
