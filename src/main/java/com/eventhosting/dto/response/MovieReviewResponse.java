package com.eventhosting.dto.response;

import com.eventhosting.entity.MovieReview;

import java.time.LocalDateTime;

public record MovieReviewResponse(
        Long reviewId,
        Long eventId,
        String eventName,
        String reviewerName,
        String reviewerEmail,
        Integer rating,
        String reviewTitle,
        String reviewText,
        LocalDateTime reviewDate
) {

    public static MovieReviewResponse from(MovieReview review) {
        return new MovieReviewResponse(
                review.getReviewId(),
                review.getHostingEvent() != null ? review.getHostingEvent().getEventId() : null,
                review.getHostingEvent() != null ? review.getHostingEvent().getEventName() : null,
                review.getReviewerName(),
                review.getReviewerEmail(),
                review.getRating(),
                review.getReviewTitle(),
                review.getReviewText(),
                review.getReviewDate()
        );
    }
}
