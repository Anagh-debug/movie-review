package com.eventhosting.dto.response;

import com.eventhosting.entity.HostingEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long eventId,
        String eventName,
        String eventType,
        String eventGenre,
        String description,
        BigDecimal baseTicketPrice,
        String attendeeName,
        String attendeeRole,
        String venue,
        String eventDate,
        LocalDateTime submissionDate,
        String approvalStatus,
        String rejectionReason,
        Long organizerId,
        String organizerName,
        String organizerEmail
) {

    public static EventResponse from(HostingEvent event) {
        return new EventResponse(
                event.getEventId(),
                event.getEventName(),
                event.getEventType(),
                event.getEventGenre(),
                event.getDescription(),
                event.getBaseTicketPrice(),
                event.getAttendeeName(),
                event.getAttendeeRole(),
                event.getVenue(),
                event.getEventDate(),
                event.getSubmissionDate(),
                event.getApprovalStatus() != null ? event.getApprovalStatus().name() : null,
                event.getRejectionReason(),
                event.getOrganizer() != null ? event.getOrganizer().getOrganizerId() : null,
                event.getOrganizer() != null ? event.getOrganizer().getOrganizerName() : null,
                event.getOrganizer() != null ? event.getOrganizer().getOrganizerEmail() : null
        );
    }
}
