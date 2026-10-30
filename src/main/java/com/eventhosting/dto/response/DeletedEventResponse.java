package com.eventhosting.dto.response;

/**
 * What a movie deletion removed: the movie itself plus the reviews that hung
 * off it.
 */
public record DeletedEventResponse(
        Long eventId,
        String eventName,
        long reviewsDeleted
) {
}
