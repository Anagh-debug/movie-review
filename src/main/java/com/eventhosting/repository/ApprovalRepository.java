package com.eventhosting.repository;

import com.eventhosting.entity.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    @Query("SELECT a FROM Approval a JOIN FETCH a.hostingEvent JOIN FETCH a.organizer "
         + "WHERE a.hostingEvent.eventId = :eventId")
    Optional<Approval> findByEventIdWithRelations(@Param("eventId") Long eventId);
}
