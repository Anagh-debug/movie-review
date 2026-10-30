package com.eventhosting.dto.response;

public record ReviewSummaryResponse(
        Long eventId,
        String eventName,
        long totalReviews,
        Double averageRating
) {
}
