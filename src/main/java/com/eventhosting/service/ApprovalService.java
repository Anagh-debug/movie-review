package com.eventhosting.service;

import com.eventhosting.dto.request.ApprovalActionRequest;
import com.eventhosting.dto.response.ApprovalResponse;
import com.eventhosting.entity.Approval;
import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.exception.BadRequestException;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.repository.ApprovalRepository;
import com.eventhosting.repository.HostingEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * The moderation half of the workflow: an administrator approves or rejects a
 * submitted event, and both the event and its approval record move together.
 */
@Service
public class ApprovalService {

    private static final Logger logger = LoggerFactory.getLogger(ApprovalService.class);

    private final HostingEventRepository hostingEventRepository;
    private final ApprovalRepository approvalRepository;

    public ApprovalService(HostingEventRepository hostingEventRepository,
                           ApprovalRepository approvalRepository) {
        this.hostingEventRepository = hostingEventRepository;
        this.approvalRepository = approvalRepository;
    }

    @Transactional
    public ApprovalResponse processApproval(Long eventId, ApprovalActionRequest request) {
        HostingEvent event = hostingEventRepository.findByIdWithOrganizer(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found with id " + eventId));

        ApprovalStatus newStatus = ApprovalStatus.from(request.getStatus());

        if (newStatus == ApprovalStatus.REJECTED
                && (request.getRejectionReason() == null || request.getRejectionReason().isBlank())) {
            throw new BadRequestException("A rejection reason is required when rejecting an event");
        }

        Approval approval = approvalRepository.findByEventIdWithRelations(eventId)
                .orElseGet(() -> new Approval(event, event.getOrganizer()));

        String reason = newStatus == ApprovalStatus.REJECTED ? request.getRejectionReason() : null;

        event.setApprovalStatus(newStatus);
        event.setRejectionReason(reason);

        approval.setHostingEvent(event);
        approval.setOrganizer(event.getOrganizer());
        approval.setApprovalStatus(newStatus);
        approval.setRejectionReason(reason);
        approval.setApprovalDate(LocalDateTime.now());
        approval.setNotificationSent(true);

        hostingEventRepository.save(event);
        Approval saved = approvalRepository.save(approval);

        logger.info("Event {} moved to {}", eventId, newStatus);
        return ApprovalResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public ApprovalResponse getApprovalByEventId(Long eventId) {
        return approvalRepository.findByEventIdWithRelations(eventId)
                .map(ApprovalResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No approval record found for event " + eventId));
    }
}
