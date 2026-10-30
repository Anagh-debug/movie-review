package com.eventhosting.service;

import com.eventhosting.dto.request.MovieReviewRequest;
import com.eventhosting.dto.response.MovieReviewResponse;
import com.eventhosting.dto.response.ReviewSummaryResponse;
import com.eventhosting.entity.HostingEvent;
import com.eventhosting.entity.MovieReview;
import com.eventhosting.exception.ResourceNotFoundException;
import com.eventhosting.repository.HostingEventRepository;
import com.eventhosting.repository.MovieReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class MovieReviewService {

    private static final Logger logger = LoggerFactory.getLogger(MovieReviewService.class);

    private final MovieReviewRepository movieReviewRepository;
    private final HostingEventRepository hostingEventRepository;

    public MovieReviewService(MovieReviewRepository movieReviewRepository,
                              HostingEventRepository hostingEventRepository) {
        this.movieReviewRepository = movieReviewRepository;
        this.hostingEventRepository = hostingEventRepository;
    }

    @Transactional
    public MovieReviewResponse addReview(Long eventId, MovieReviewRequest request) {
        HostingEvent event = requireEvent(eventId);

        MovieReview review = new MovieReview(
                event,
                request.getReviewerName().trim(),
                request.getReviewerEmail() != null ? request.getReviewerEmail().trim() : null,
                request.getRating(),
                request.getReviewTitle(),
                request.getReviewText()
        );

        MovieReview saved = movieReviewRepository.save(review);
        logger.info("Review added: id={} eventId={} rating={}",
                saved.getReviewId(), eventId, saved.getRating());

        return MovieReviewResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<MovieReviewResponse> getReviewsForEvent(Long eventId) {
        requireEvent(eventId);
        return movieReviewRepository.findByEventIdWithEvent(eventId)
                .stream()
                .map(MovieReviewResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieReviewResponse getReviewById(Long reviewId) {
        return movieReviewRepository.findById(reviewId)
                .map(MovieReviewResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id " + reviewId));
    }

    @Transactional(readOnly = true)
    public ReviewSummaryResponse getSummaryForEvent(Long eventId) {
        HostingEvent event = requireEvent(eventId);

        long total = movieReviewRepository.countByHostingEvent_EventId(eventId);
        Double average = movieReviewRepository.findAverageRatingByEventId(eventId);

        // AVG returns null when the event has no reviews yet.
        Double rounded = average == null ? null
                : BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP).doubleValue();

        return new ReviewSummaryResponse(eventId, event.getEventName(), total, rounded);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!movieReviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id " + reviewId);
        }
        movieReviewRepository.deleteById(reviewId);
        logger.info("Review deleted: id={}", reviewId);
    }

    private HostingEvent requireEvent(Long eventId) {
        return hostingEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found with id " + eventId));
    }
}
