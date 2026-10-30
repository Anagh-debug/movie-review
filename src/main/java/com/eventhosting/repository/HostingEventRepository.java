package com.eventhosting.repository;

import com.eventhosting.entity.ApprovalStatus;
import com.eventhosting.entity.HostingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Read queries JOIN FETCH the organizer so listing events does not fire one
 * extra select per row (N+1).
 */
public interface HostingEventRepository extends JpaRepository<HostingEvent, Long> {

    @Query("SELECT e FROM HostingEvent e JOIN FETCH e.organizer "
         + "ORDER BY e.submissionDate DESC")
    List<HostingEvent> findAllWithOrganizer();

    @Query("SELECT e FROM HostingEvent e JOIN FETCH e.organizer "
         + "WHERE e.approvalStatus = :status ORDER BY e.submissionDate DESC")
    List<HostingEvent> findByStatusWithOrganizer(@Param("status") ApprovalStatus status);

    @Query("SELECT e FROM HostingEvent e JOIN FETCH e.organizer WHERE e.eventId = :eventId")
    Optional<HostingEvent> findByIdWithOrganizer(@Param("eventId") Long eventId);

    long countByApprovalStatus(ApprovalStatus approvalStatus);
}
