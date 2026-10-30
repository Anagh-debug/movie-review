package com.eventhosting.repository;

import com.eventhosting.entity.MovieReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JOIN FETCH on the event keeps a list of reviews from firing one extra select
 * per row (N+1) when the event name is rendered alongside each review.
 */
public interface MovieReviewRepository extends JpaRepository<MovieReview, Long> {

    @Query("SELECT r FROM MovieReview r JOIN FETCH r.hostingEvent "
         + "WHERE r.hostingEvent.eventId = :eventId ORDER BY r.reviewDate DESC")
    List<MovieReview> findByEventIdWithEvent(@Param("eventId") Long eventId);

    @Query("SELECT AVG(r.rating) FROM MovieReview r WHERE r.hostingEvent.eventId = :eventId")
    Double findAverageRatingByEventId(@Param("eventId") Long eventId);

    long countByHostingEvent_EventId(Long eventId);

    /**
     * Clears a movie's reviews before the movie itself is removed; the review
     * table holds a non-null foreign key to the event, so they cannot outlive it.
     */
    long deleteByHostingEvent_EventId(Long eventId);
}
