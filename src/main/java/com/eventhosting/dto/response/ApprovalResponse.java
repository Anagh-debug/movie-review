package com.eventhosting.dto.response;

import com.eventhosting.entity.Approval;

import java.time.LocalDateTime;

public record ApprovalResponse(
        Long approvalId,
        Long eventId,
        String eventName,
        Long organizerId,
        String organizerName,
        String approvalStatus,
        String rejectionReason,
        LocalDateTime approvalDate,
        Boolean notificationSent
) {

    public static ApprovalResponse from(Approval approval) {
        return new ApprovalResponse(
                approval.getApprovalId(),
                approval.getHostingEvent() != null ? approval.getHostingEvent().getEventId() : null,
                approval.getHostingEvent() != null ? approval.getHostingEvent().getEventName() : null,
                approval.getOrganizer() != null ? approval.getOrganizer().getOrganizerId() : null,
                approval.getOrganizer() != null ? approval.getOrganizer().getOrganizerName() : null,
                approval.getApprovalStatus() != null ? approval.getApprovalStatus().name() : null,
                approval.getRejectionReason(),
                approval.getApprovalDate(),
                approval.getNotificationSent()
        );
    }
}
